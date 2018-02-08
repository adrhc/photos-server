package image.exifweb.album.export;

import image.exifweb.album.AlbumRepository;
import image.exifweb.persistence.Album;
import image.exifweb.util.deferredresult.KeyValueDeferredResult;
import image.exifweb.util.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.DeferredResult;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/json/album")
public class AlbumExporterCtrl {
	private static final Logger logger = LoggerFactory.getLogger(AlbumExporterCtrl.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");
	@Inject
	private ThreadPoolTaskExecutor asyncExecutor;
	@Inject
	private AlbumRepository albumRepository;
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
			switch (albumExporterService.writeJsonForAllAlbumsSafe()) {
				case fail:
					deferredResult.setResult("message", "All JSON files NOT updated!");
					break;
				case success:
					deferredResult.setResult("message", "All JSON files updated!");
					break;
				case partial:
					deferredResult.setResult("message", "Some JSON files updated some NOT!");
			}
			logger.debug("[updateJsonForAllAlbums] END");
		}, asyncExecutor);
	}

	@RequestMapping(value = "/updateJsonForAlbum", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public DeferredResult<Map<String, String>> updateJsonFor1Album(@RequestBody JsonValue jsonValue) {
		logger.debug("BEGIN {}", jsonValue.getValue());
		return KeyValueDeferredResult.of((deferredResult) -> {
			if (albumExporterService.writeJsonForAlbumSafe(jsonValue.getValue())) {
				deferredResult.setResult("message",
						"JSON files updated for album " + jsonValue.getValue() + "!");
			} else {
				deferredResult.setResult("message",
						"JSON files NOT updated for album " + jsonValue.getValue() + "!");
			}
			logger.debug("[updateJsonFor1Album] END");
		}, asyncExecutor);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public Album getAlbumById(@PathVariable Integer id, WebRequest webRequest) {
		logger.debug("BEGIN {}", id);
		Album album = albumRepository.getAlbumById(id);
		if (webRequest.checkNotModified(album.getLastUpdate().getTime())) {
			return null;
		}
		logger.debug("END album ({}) modified since: {}", id, sdf.format(album.getLastUpdate()));
		return album;
	}

	@RequestMapping(value = "/byName/{name}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public Album getAlbumByName(@PathVariable String name, WebRequest webRequest) {
		logger.debug("BEGIN {}", name);
		Album album = albumRepository.getAlbumByName(name);
		if (webRequest.checkNotModified(album.getLastUpdate().getTime())) {
			return null;
		}
		logger.debug("END album ({}) modified since: {}", name, sdf.format(album.getLastUpdate()));
		return album;
	}
}
