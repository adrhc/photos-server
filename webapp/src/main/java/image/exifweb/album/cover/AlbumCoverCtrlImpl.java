package image.exifweb.album.cover;

import image.cdm.album.cover.AlbumCover;
import image.exifweb.web.controller.INotModifiedChecker;
import image.jpa2x.repositories.album.AlbumCoverRepository;
import image.jpa2x.repositories.album.AlbumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

/**
 * Created by adr on 2/5/18.
 */
@RestController
@RequestMapping("/json/cover")
public class AlbumCoverCtrlImpl implements INotModifiedChecker, AlbumCoverCtrl {
	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverCtrlImpl.class);

	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private AlbumCoverRepository albumCoverRepository;

	@Override
	@RequestMapping(method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public List<AlbumCover> getAllCovers(WebRequest webRequest) {
		logger.debug("BEGIN");
		return this.checkNotModified(this.albumRepository::getMaxLastUpdateForAll,
				this.albumCoverRepository::getCovers, webRequest);
	}

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public AlbumCover getAlbumCoverById(@PathVariable Integer id, WebRequest webRequest) {
		logger.debug("BEGIN {}", id);
		return this.checkNotModified(() -> this.albumCoverRepository.getCoverById(id),
				AlbumCover::getLastUpdate, webRequest);
	}

	@Override
	@RequestMapping(value = "/search", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public AlbumCover searchAlbumCover(@RequestParam String name, WebRequest webRequest) {
		logger.debug("BEGIN {}", name);
		return this.checkNotModified(() -> this.albumCoverRepository.getCoverByName(name),
				AlbumCover::getLastUpdate, webRequest);
	}
}
