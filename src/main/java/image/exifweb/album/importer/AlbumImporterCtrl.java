package image.exifweb.album.importer;

import image.exifweb.album.events.AlbumEventsEmitter;
import image.exifweb.album.events.EAlbumEventType;
import image.exifweb.persistence.Album;
import image.exifweb.util.deferredresult.KeyValueDeferredResult;
import image.exifweb.util.json.JsonValue;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by adr on 2/6/18.
 */
@RestController
@RequestMapping("/json/import")
public class AlbumImporterCtrl {
	private static final Logger logger = LoggerFactory.getLogger(AlbumImporterCtrl.class);
	@Inject
	private ThreadPoolTaskExecutor asyncExecutor;
	@Inject
	private AlbumImporter albumImporter;
	@Inject
	private AlbumEventsEmitter albumEventsEmitter;

	@RequestMapping(value = "/reImport", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public DeferredResult<Map<String, String>> reImport(@RequestBody JsonValue json1Value) {
		logger.debug("BEGIN {}", json1Value.getValue());
		return KeyValueDeferredResult.of((deferredResult) -> {
			if (StringUtils.hasText(json1Value.getValue())) {
				albumImporter.importAlbumByName(json1Value.getValue());
				deferredResult.setResult("message", "Reimported " + json1Value.getValue());
			} else {
				albumImporter.importAllFromAlbumsRoot();
				deferredResult.setResult("message", "Reimported all albums");
			}
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
					.albumEventsByTypes(true, EnumSet.of(EAlbumEventType.ALBUM_IMPORTED))
					.take(1L).subscribe(
							ae -> newAlbums.add(ae.getAlbum()),
							t -> {
								logger.error(t.getMessage(), t);
								logger.error("[ALBUM_IMPORTED] newAlbums");
							});
			albumImporter.importNewAlbumsOnly();
			logger.debug("BEGIN importedAlbums.size = {}", newAlbums.size());
			if (newAlbums.isEmpty()) {
				deferredResult.setResult("message", "No new album to import!");
			} else {
				deferredResult.setResult("message", "Albums imported for: " +
						newAlbums.stream().map(Album::getName)
								.collect(Collectors.joining(", ")));
			}
			subscription.dispose();
			logger.debug("[importNewAlbumsOnly] END");
		}, asyncExecutor);
	}

}
