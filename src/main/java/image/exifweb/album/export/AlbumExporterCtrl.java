package image.exifweb.album.export;

import image.exifweb.album.AlbumRepository;
import image.exifweb.util.frameworks.spring.KeyValueDeferredResult;
import image.exifweb.util.json.JsonStringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

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
				put(E3ResultTypes.FAIL, "All JSON files NOT updated!");
			}};

	@Inject
	private ThreadPoolTaskExecutor asyncExecutor;
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
			if (albumExporterService.writeJsonForAlbumSafe(jsonStringValue.getValue())) {
				deferredResult.setResult("message",
						"JSON files updated for album " + jsonStringValue.getValue() + "!");
			} else {
				deferredResult.setResult("message",
						"JSON files NOT updated for album " + jsonStringValue.getValue() + "!");
			}
			logger.debug("[updateJsonFor1Album] END");
		}, asyncExecutor);
	}
}
