package image.exifweb.album.importer;

import image.exifweb.system.events.album.AlbumEventsEmitter;
import image.exifweb.util.json.JsonStringValue;
import image.exifweb.web.controller.KeyValueDeferredResult;
import image.persistence.entity.Album;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static image.exifweb.system.events.album.EAlbumEventType.ALBUM_IMPORTED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by adr on 2/6/18.
 */
@RestController
@RequestMapping("/json/import")
public class AlbumImporterCtrl {
	private static final Logger logger = LoggerFactory.getLogger(AlbumImporterCtrl.class);
	private static final MessageFormat REIMPORT_MSG_PATTERN = new MessageFormat("Reimported {0}");
	@Inject
	private Executor asyncExecutor;
	@Inject
	private AlbumImporterService albumImporterService;
	/**
	 * Boolean = "albumName is empty?"
	 */
	private final Map<Boolean, BiConsumer<String, KeyValueDeferredResult<String, String>>>
			REIMPORT_CHOICES =
			new HashMap<Boolean, BiConsumer<String, KeyValueDeferredResult<String, String>>>() {{
				put(TRUE, (albumName, deferredResult) -> {
					albumImporterService.importAlbumByName(albumName);
					deferredResult.setResult("message",
							REIMPORT_MSG_PATTERN.format(new Object[]{albumName}));
				});
				put(FALSE, (albumName, deferredResult) -> {
					albumImporterService.importAllFromAlbumsRoot();
					deferredResult.setResult("message",
							REIMPORT_MSG_PATTERN.format(new Object[]{"all albums"}));
				});
			}};
	@Inject
	private AlbumEventsEmitter albumEventsEmitter;

	@RequestMapping(value = "/reImport", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public DeferredResult<Map<String, String>> reImport(@RequestBody JsonStringValue json1Value) {
		logger.debug("BEGIN {}", json1Value.getValue());
		return KeyValueDeferredResult.of((deferredResult) -> {
			String albumName = json1Value.getValue();
			REIMPORT_CHOICES.get(StringUtils.hasText(albumName))
					.accept(albumName, deferredResult);
			logger.debug("[reImport] END {}", json1Value.getValue());
		}, asyncExecutor);
	}

	@RequestMapping(value = "/importNewAlbumsOnly", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public DeferredResult<Map<String, String>> importNewAlbumsOnly() {
		logger.debug("BEGIN");
		return KeyValueDeferredResult.of((deferredResult) -> {
			List<Album> newAlbums = new ArrayList<>();
			Disposable subscription = albumEventsEmitter
					.albumEventsByTypes(true, ALBUM_IMPORTED)
					.take(1L)
					.subscribe(
							ae -> newAlbums.add(ae.getAlbum()),
							t -> {
								logger.error(t.getMessage(), t);
								logger.error("[ALBUM_IMPORTED] newAlbums");
							});
			albumImporterService.importNewAlbumsOnly();
			logger.debug("BEGIN importedAlbums.size = {}", newAlbums.size());
			if (newAlbums.isEmpty()) {
				deferredResult.setResult("message", "No new album to import!");
			} else {
				deferredResult.setResult("message", "Albums imported for: " +
						newAlbums.stream().map(Album::getName)
								.collect(Collectors.joining(", ")));
			}
			// todo: make sure to dispose even when an exception occurs
			subscription.dispose();
			logger.debug("[importNewAlbumsOnly] END");
		}, asyncExecutor);
	}

}
