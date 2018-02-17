package image.exifweb.album.cover;

import image.exifweb.system.persistence.repositories.AlbumCoverRepository;
import image.exifweb.web.controller.INotModifiedChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by adr on 2/5/18.
 */
@RestController
@RequestMapping("/json/cover")
public class AlbumCoverCtrl implements INotModifiedChecker {
	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverCtrl.class);

	@Inject
	private AlbumCoverRepository albumCoverRepository;
	@Inject
	private AlbumCoverService albumCoverService;

	@RequestMapping(method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<AlbumCover> getAllCovers(WebRequest webRequest) {
		logger.debug("BEGIN");
		return checkNotModified(albumCoverRepository::getAlbumCoversLastUpdateDate,
				albumCoverService::getCovers, webRequest);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public AlbumCover getAlbumCoverById(@PathVariable Integer id, WebRequest webRequest) {
		logger.debug("BEGIN {}", id);
		return checkNotModified(() -> albumCoverService.getCoverById(id),
				AlbumCover::getLastUpdate, webRequest);
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public AlbumCover searchAlbumCover(@RequestParam String name, WebRequest webRequest) {
		logger.debug("BEGIN {}", name);
		return checkNotModified(() -> albumCoverService.getCoverByName(name),
				AlbumCover::getLastUpdate, webRequest);
	}
}
