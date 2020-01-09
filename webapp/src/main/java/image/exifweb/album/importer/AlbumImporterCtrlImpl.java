package image.exifweb.album.importer;

import image.exifweb.web.controller.KeyValueDeferredResult;
import image.exifweb.web.deferred.DeferredResultUtils;
import image.exifweb.web.json.JsonStringValue;
import image.infrastructure.messaging.album.AlbumEvent;
import image.photos.album.services.AlbumImporterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneaked;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.util.StringUtils.hasText;

/**
 * Created by adr on 2/6/18.
 */
@RestController
@RequestMapping("/json/import")
public class AlbumImporterCtrlImpl implements AlbumImporterCtrl {
	private static final Logger logger = LoggerFactory.getLogger(AlbumImporterCtrlImpl.class);
	private final Executor asyncExecutor;
	private final AlbumImporterService albumImporterService;
	/**
	 * Boolean = "albumName has text?"
	 */
	private Map<Boolean, BiConsumer<String, KeyValueDeferredResult<String, String>>> REIMPORT_CHOICES;

	public AlbumImporterCtrlImpl(Executor asyncExecutor, AlbumImporterService albumImporterService) {
		this.asyncExecutor = asyncExecutor;
		this.albumImporterService = albumImporterService;
	}

	private static String joinAlbumNames(List<Optional<AlbumEvent>> albumEvents) {
		return albumEvents.stream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(AlbumImporterCtrlImpl::importFeedBack)
				.collect(Collectors.joining(", "));
	}

	private static String importFeedBack(AlbumEvent event) {
		switch (event.getType()) {
			case FAILED_UPDATE:
				return event.getEntity().getName() + " failed";
			case NEW_BUT_EMPTY:
				return event.getEntity().getName() + " is new but empty";
		}
		return event.getEntity().getName();
	}

	@Override
	@PostMapping(value = "/reImport", produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public DeferredResult<Map<String, String>> reImport(
			@RequestBody(required = false) JsonStringValue json1Value) {
		String albumName = json1Value == null ? null : json1Value.getValue();
		logger.debug("BEGIN {}", albumName == null ? "all" : albumName);
		return KeyValueDeferredResult.of((deferredResult) -> {
			this.REIMPORT_CHOICES.get(hasText(albumName))
					.accept(albumName, deferredResult);
			logger.debug("[reImport] END {}", albumName == null ? "all" : albumName);
		}, this.asyncExecutor);
	}

	@Override
	@PostMapping(value = "/importNewAlbumsOnly", produces = APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public DeferredResult<Map<String, String>> importNewAlbumsOnly() {
		logger.debug("BEGIN");
		return KeyValueDeferredResult.of((deferredResult) -> {
			// this must be blocking in order not to immediately dispose
			var albumEvents = DeferredResultUtils.getOrFail(
					sneaked(this.albumImporterService::importNewAlbums),
					"New albums import failed!", deferredResult);
			if (deferredResult.isSetOrExpired()) {
				return;
			}
			String names = joinAlbumNames(albumEvents);
			logger.debug("imported albums:\n{}", names);
			deferredResult.setResult("message", "imported albums: " + names);
		}, this.asyncExecutor);
	}

	private void importByAlbumName(String albumName,
			KeyValueDeferredResult<String, String> deferredResult) {
		Optional<AlbumEvent> albumEvent = this
				.albumImporterService.importByAlbumName(albumName);
		albumEvent.ifPresentOrElse(
				event -> {
					logger.debug("{} {}", albumName, importFeedBack(event));
					deferredResult.setResult("message",
							"Reimported album: " + importFeedBack(event));
				},
				() -> {
					logger.error("{} is empty! (re)import failed!", albumName);
					deferredResult.setResult("message", albumName + " is empty! (re)import failed!");
				}
		);
	}

	private void importAll(KeyValueDeferredResult<String, String> deferredResult) {
		var albumEvents = DeferredResultUtils.getOrFail(
				sneaked(this.albumImporterService::importAll),
				"New albums import failed!", deferredResult);
		if (deferredResult.isSetOrExpired()) {
			return;
		}
		String names = joinAlbumNames(albumEvents);
		logger.debug("albums re/imported:\n{}", names);
		deferredResult.setResult("message", "Reimported albums: " + (hasText(names) ? names : "none"));
	}

	@PostConstruct
	public void postConstruct() {
		this.REIMPORT_CHOICES =
				new HashMap<>() {{
					this.put(TRUE, AlbumImporterCtrlImpl.this::importByAlbumName);
					this.put(FALSE, (albumName, deferredResult) ->
							AlbumImporterCtrlImpl.this.importAll(deferredResult));
				}};
	}
}
