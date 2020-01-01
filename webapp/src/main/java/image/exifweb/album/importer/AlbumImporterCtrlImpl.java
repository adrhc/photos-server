package image.exifweb.album.importer;

import image.exifweb.web.controller.KeyValueDeferredResult;
import image.exifweb.web.json.JsonStringValue;
import image.photos.album.importing.AlbumImporterService;
import image.photos.events.album.AlbumTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.Disposable;

import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

import static image.photos.events.album.AlbumEventTypeEnum.CREATED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by adr on 2/6/18.
 */
@RestController
@RequestMapping("/json/import")
public class AlbumImporterCtrlImpl implements AlbumImporterCtrl {
	private static final Logger logger = LoggerFactory.getLogger(AlbumImporterCtrlImpl.class);
	private static final MessageFormat REIMPORT_MSG_PATTERN = new MessageFormat("Reimported {0}");
	@Autowired
	private Executor asyncExecutor;
	@Autowired
	private AlbumImporterService albumImporterService;
	/**
	 * Boolean = "albumName is empty?"
	 */
	private final Map<Boolean, BiConsumer<String, KeyValueDeferredResult<String, String>>>
			REIMPORT_CHOICES =
			new HashMap<>() {{
				put(TRUE, (albumName, deferredResult) -> {
					AlbumImporterCtrlImpl.this.albumImporterService.importByAlbumName(albumName);
					deferredResult.setResult("message",
							REIMPORT_MSG_PATTERN.format(new Object[]{albumName}));
				});
				put(FALSE, (albumName, deferredResult) -> {
					AlbumImporterCtrlImpl.this.albumImporterService.importAll();
					deferredResult.setResult("message",
							REIMPORT_MSG_PATTERN.format(new Object[]{"all albums"}));
				});
			}};
	@Autowired
	private AlbumTopic albumTopic;

	@Override
	@RequestMapping(value = "/reImport", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public DeferredResult<Map<String, String>> reImport(@RequestBody JsonStringValue json1Value) {
		logger.debug("BEGIN {}", json1Value.getValue());
		return KeyValueDeferredResult.of((deferredResult) -> {
			String albumName = json1Value.getValue();
			this.REIMPORT_CHOICES.get(StringUtils.hasText(albumName))
					.accept(albumName, deferredResult);
			logger.debug("[reImport] END {}", json1Value.getValue());
		}, this.asyncExecutor);
	}

	@Override
	@RequestMapping(value = "/importNewAlbumsOnly", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public DeferredResult<Map<String, String>> importNewAlbumsOnly() {
		logger.debug("BEGIN");
		return KeyValueDeferredResult.of((deferredResult) -> {
			Disposable subscription = this.albumTopic
					.albumEventsByTypes(true, EnumSet.of(CREATED))
					.take(1L)// todo: take all new albums imported
					.subscribe(
							ae -> {
								logger.debug("imported album: {}", ae.getAlbum().getName());
								deferredResult.setResult("message",
										"imported album: " + ae.getAlbum().getName());
							},
							t -> {
								logger.error(t.getMessage(), t);
								logger.error("Error while trying to import new albums!");
							},
							() -> deferredResult.setResult("message", "No new album to import!"));
			// this must be blocking in order not to immediately dispose
			this.albumImporterService.importNewAlbums();
			// todo: make sure to dispose even when an exception occurs
			subscription.dispose();
		}, this.asyncExecutor);
	}

}
