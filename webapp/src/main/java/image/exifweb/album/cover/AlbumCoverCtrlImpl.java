package image.exifweb.album.cover;

import image.cdm.album.cover.AlbumCover;
import image.exifweb.web.controller.INotModifiedChecker;
import image.persistence.repositories.AlbumRepository;
import image.photos.album.AlbumCoverService;
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
public class AlbumCoverCtrlImpl implements INotModifiedChecker, AlbumCoverCtrl {
	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverCtrlImpl.class);

	@Inject
	private AlbumRepository albumRepository;
	@Inject
	private AlbumCoverService albumCoverService;

	@Override
	@RequestMapping(method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<AlbumCover> getAllCovers(WebRequest webRequest) {
		logger.debug("BEGIN");
		return checkNotModified(this.albumRepository::getAlbumCoversLastUpdateDate,
				this.albumCoverService::getCovers, webRequest);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public AlbumCover getAlbumCoverById(@PathVariable Integer id, WebRequest webRequest) {
		logger.debug("BEGIN {}", id);
		return checkNotModified(() -> this.albumCoverService.getCoverById(id),
				AlbumCover::getLastUpdate, webRequest);
	}

	@Override
	@RequestMapping(value = "/search", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public AlbumCover searchAlbumCover(@RequestParam String name, WebRequest webRequest) {
		logger.debug("BEGIN {}", name);
		return checkNotModified(() -> this.albumCoverService.getCoverByName(name),
				AlbumCover::getLastUpdate, webRequest);
	}
}
