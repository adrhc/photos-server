package image.exifweb.album;

import image.exifweb.exif.AlbumImportService;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.view.AlbumCover;
import image.exifweb.util.deferredresult.KeyValueDeferredResult;
import image.exifweb.util.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.DeferredResult;

import javax.inject.Inject;
import java.io.IOException;
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

	@RequestMapping(value = "/importAlbums", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public DeferredResult<Map<String, String>> importNewAlbumsOnly() throws IOException {
		logger.debug("BEGIN");
		KeyValueDeferredResult<String, String> deferredResult = new KeyValueDeferredResult<>();
		albumImportService.importNewAlbumsOnly(importedAlbums -> {
			logger.debug("BEGIN importedAlbums.size = {}", importedAlbums.size());
			if (importedAlbums.isEmpty()) {
				deferredResult.setResult("message", "No new album to import!");
			} else {
				deferredResult.setResult("message", "Albums imported for: " +
						importedAlbums.stream().map(Album::getName)
								.collect(Collectors.joining(", ")));
			}
		});
		logger.debug("END");
		return deferredResult;
	}

	@RequestMapping(value = "/writeJsonForAlbumsPage", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void writeJsonForAlbumsPage(Model model) throws IOException {
		logger.debug("BEGIN");
		albumService.writeJsonForAlbumsPage();
		model.addAttribute("message", AlbumService.ALBUMS_PAGE_JSON + " updated!");
	}

	@RequestMapping(value = "/updateJsonForAllAlbums", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateJsonForAllAlbums(Model model) throws IOException {
		logger.debug("BEGIN");
		albumService.writeJsonForAllAlbums();
		model.addAttribute("message", "JSON files updated!");
	}

	@RequestMapping(value = "/updateJsonForAlbum", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateJsonForAlbum(@RequestBody JsonValue jsonValue, Model model) {
		logger.debug("BEGIN");
		if (albumService.writeJsonForAlbumSafe(jsonValue.getValue())) {
			model.addAttribute("message",
					"JSON files NOT updated for album " + jsonValue.getValue() + "!");
		} else {
			model.addAttribute("message",
					"JSON files updated for album " + jsonValue.getValue() + "!");
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Album get(@PathVariable Integer id, WebRequest webRequest) {
		Album album = albumService.getAlbumById(id);
		if (webRequest.checkNotModified(album.getLastUpdate().getTime())) {
			return null;
		}
		return album;
	}

	@RequestMapping(method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public List<AlbumCover> getAllCovers(WebRequest webRequest) {
		if (webRequest.checkNotModified(albumService.getLastUpdatedForAlbums().getTime())) {
			return null;
		}
		return albumService.getAllCovers(true);
	}
}
