package image.exifweb.album.importer;

import image.exifweb.web.controller.KeyValueDeferredResult;
import image.exifweb.web.json.JsonStringValue;
import image.infrastructure.messaging.album.AlbumEvent;
import image.photos.album.services.AlbumImporterService;
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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

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
	private final Executor asyncExecutor;
	private final AlbumImporterService albumImporterService;
	/**
	 * Boolean = "albumName is empty?"
	 */
	private final Map<Boolean, BiConsumer<String, KeyValueDeferredResult<String, String>>>
			REIMPORT_CHOICES =
			new HashMap<>() {{
				put(TRUE, (albumName, deferredResult) -> {
					Optional<AlbumEvent> albumEvent = AlbumImporterCtrlImpl.this
							.albumImporterService.importByAlbumName(albumName);
					albumEvent.ifPresentOrElse(
							ae -> {
								logger.debug("reimported {}", albumName);
								deferredResult.setResult("message",
										REIMPORT_MSG_PATTERN.format(new Object[]{albumEvent}));
							},
							() -> {
								logger.error("{} reimport failed!", albumName);
								deferredResult.setResult("message", albumName + " reimport failed!");
							}
					);
				});
				put(FALSE, (albumName, deferredResult) -> {
					var albumEvents = AlbumImporterCtrlImpl.this
							.albumImporterService.importAll();
					String names = joinAlbumNames(albumEvents);
					logger.debug("albums re/imported:\n{}", names);
					deferredResult.setResult("message", REIMPORT_MSG_PATTERN
							.format(new Object[]{"all albums (" + names + ")"}));
				});
			}};

	public AlbumImporterCtrlImpl(Executor asyncExecutor, AlbumImporterService albumImporterService) {
		this.asyncExecutor = asyncExecutor;
		this.albumImporterService = albumImporterService;
	}

	private static String joinAlbumNames(List<Optional<AlbumEvent>> albumEvents) {
		return albumEvents.stream()
				.filter(Optional::isPresent)
				.map(e -> e.get().getEntity().getName())
				.collect(Collectors.joining(", "));
	}

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
			// this must be blocking in order not to immediately dispose
			var albumEvents = this.albumImporterService.importNewAlbums();
			String names = joinAlbumNames(albumEvents);
			logger.debug("imported albums:\n{}", names);
			deferredResult.setResult("message", "imported albums: " + names);
		}, this.asyncExecutor);
	}
}
