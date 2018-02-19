package image.exifweb.album.export;

import image.exifweb.util.json.JsonStringValue;
import image.exifweb.web.controller.KeyValueDeferredResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Function;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/json/exporter")
public class AlbumExporterCtrl {
	private static final Logger logger = LoggerFactory.getLogger(AlbumExporterCtrl.class);

	private static final Map<E3ResultTypes, String> ALL_ALBUMS_JSON_UPDATE_MSG =
			new HashMap<E3ResultTypes, String>() {{
				put(E3ResultTypes.SUCCESS, "All JSON files updated!");
				put(E3ResultTypes.PARTIAL, "Some JSON files updated some NOT!");
				put(E3ResultTypes.FAIL, "No JSON file updated!");
			}};
	private static final MessageFormat ALBUM_JSON_UPDATE_MSG_PATTERN =
			new MessageFormat("JSON files {0}updated for album {1}!");
	private static final Map<Boolean, Function<String, String>> ALBUM_JSON_UPDATE_MSG =
			new HashMap<Boolean, Function<String, String>>() {{
				put(TRUE, albumName -> ALBUM_JSON_UPDATE_MSG_PATTERN.format(new Object[]{"", albumName}));
				put(FALSE, albumName -> ALBUM_JSON_UPDATE_MSG_PATTERN.format(new Object[]{"NOT ", albumName}));
			}};
	@Inject
	private Executor asyncExecutor;
	@Inject
	private AlbumExporterService albumExporterService;

	@RequestMapping(value = "/writeJsonForAlbumsPage", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public DeferredResult<Map<String, String>> updateJsonForAlbumsPage() {
		logger.debug("BEGIN");
		return KeyValueDeferredResult.of((deferredResult) -> {
			albumExporterService.writeJsonForAlbumsPageSafe();
			deferredResult.setResult("message", AlbumExporterService.ALBUMS_PAGE_JSON + " updated!");
			logger.debug("[updateJsonForAlbumsPage] END");
		}, asyncExecutor);
	}

	@RequestMapping(value = "/updateJsonForAllAlbums", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public DeferredResult<Map<String, String>> updateJsonForAllAlbums() {
		logger.debug("BEGIN");
		return KeyValueDeferredResult.of((deferredResult) -> {
			E3ResultTypes e3Result = albumExporterService.writeJsonForAllAlbumsSafe();
			deferredResult.setResult("message", ALL_ALBUMS_JSON_UPDATE_MSG.get(e3Result));
			logger.debug("[updateJsonForAllAlbums] END");
		}, asyncExecutor);
	}

	@RequestMapping(value = "/updateJsonForAlbum", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public DeferredResult<Map<String, String>> updateJsonFor1Album(@RequestBody JsonStringValue jsonStringValue) {
		logger.debug("BEGIN {}", jsonStringValue.getValue());
		return KeyValueDeferredResult.of((deferredResult) -> {
			Boolean success = albumExporterService
					.writeJsonForAlbumSafe(jsonStringValue.getValue());
			deferredResult.setResult("message",
					ALBUM_JSON_UPDATE_MSG.get(success).apply(jsonStringValue.getValue()));
			logger.debug("[updateJsonFor1Album] END");
		}, asyncExecutor);
	}
}
