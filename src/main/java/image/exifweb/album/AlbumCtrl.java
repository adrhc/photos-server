package image.exifweb.album;

import image.exifweb.exif.AlbumImportService;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.view.AlbumCover;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/json/album")
public class AlbumCtrl {
	private static final Logger logger = LoggerFactory.getLogger(AlbumCtrl.class);
	@Inject
	private ThreadPoolTaskExecutor asyncExecutor;
	@Inject
	private AlbumService albumService;
	@Inject
	private AlbumImportService albumImportService;

	@RequestMapping(value = "/importAlbums", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public DeferredResult<Map<String, String>> importNewAlbumsOnly() {
		logger.debug("BEGIN");
		return KeyValueDeferredResult.of((deferredResult) ->
				albumImportService.importNewAlbumsOnly(importedAlbums -> {
					logger.debug("BEGIN importedAlbums.size = {}", importedAlbums.size());
					if (importedAlbums.isEmpty()) {
						deferredResult.setResult("message", "No new album to import!");
					} else {
						deferredResult.setResult("message", "Albums imported for: " +
								importedAlbums.stream().map(Album::getName)
										.collect(Collectors.joining(", ")));
					}
				}), asyncExecutor);
	}

	@RequestMapping(value = "/writeJsonForAlbumsPage", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public DeferredResult<Map<String, String>> updateJsonForAlbumsPage() {
		logger.debug("BEGIN");
		return KeyValueDeferredResult.of((deferredResult) -> {
			albumService.writeJsonForAlbumsPageSafe();
			deferredResult.setResult("message", AlbumService.ALBUMS_PAGE_JSON + " updated!");
		}, asyncExecutor);
	}

	@RequestMapping(value = "/updateJsonForAllAlbums", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public DeferredResult<Map<String, String>> updateJsonForAllAlbums() {
		logger.debug("BEGIN");
		return KeyValueDeferredResult.of((deferredResult) -> {
			switch (albumService.writeJsonForAllAlbumsSafe()) {
				case fail:
					deferredResult.setResult("message", "All JSON files NOT updated!");
					break;
				case success:
					deferredResult.setResult("message", "All JSON files updated!");
					break;
				case partial:
					deferredResult.setResult("message", "Some JSON files updated some NOT!");
			}
		}, asyncExecutor);
	}

	@RequestMapping(value = "/updateJsonForAlbum", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public DeferredResult<Map<String, String>> updateJsonFor1Album(@RequestBody JsonValue jsonValue) {
		logger.debug("BEGIN");
		return KeyValueDeferredResult.of((deferredResult) -> {
			if (albumService.writeJsonForAlbumSafe(jsonValue.getValue())) {
				deferredResult.setResult("message",
						"JSON files NOT updated for album " + jsonValue.getValue() + "!");
			} else {
				deferredResult.setResult("message",
						"JSON files updated for album " + jsonValue.getValue() + "!");
			}
		}, asyncExecutor);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public Album get(@PathVariable Integer id, WebRequest webRequest) {
		Album album = albumService.getAlbumById(id);
		if (webRequest.checkNotModified(album.getLastUpdate().getTime())) {
			return null;
		}
		return album;
	}

	@RequestMapping(method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public List<AlbumCover> getAllCovers(WebRequest webRequest) {
		if (webRequest.checkNotModified(albumService.getLastUpdatedForAlbums().getTime())) {
			return null;
		}
		return albumService.getAllCovers(true);
	}
}
