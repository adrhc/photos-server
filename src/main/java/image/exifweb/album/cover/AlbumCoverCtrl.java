package image.exifweb.album.cover;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by adr on 2/5/18.
 */
@RestController
@RequestMapping("/json/cover")
public class AlbumCoverCtrl {
	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverCtrl.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");

	@Inject
	private AlbumCoverRepo albumCoverRepo;
	@Inject
	private AlbumCoverComp albumCoverComp;

	/**
	 * Pt a testa checkNotModified TREBUIE ca browser cache sa fie activat!
	 *
	 * @param webRequest
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<AlbumCover> getAllCovers(WebRequest webRequest) {
//		logger.debug("BEGIN");
		Date albumCoversLastUpdateDate = albumCoverRepo.getAlbumCoversLastUpdateDate();
		if (webRequest.checkNotModified(albumCoversLastUpdateDate.getTime())) {
//			logger.debug("not modified since: {}",
//					sdf.format(albumService.getAlbumCoversLastUpdateDate()));
			return null;
		}
		logger.debug("covers modified since: {}", sdf.format(albumCoversLastUpdateDate));
		return albumCoverComp.getCovers();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public AlbumCover getAlbumById(@PathVariable Integer id, WebRequest webRequest) {
		logger.debug("BEGIN {}", id);
		AlbumCover albumCover = albumCoverComp.getCoverById(id);
		if (webRequest.checkNotModified(albumCover.getLastUpdate().getTime())) {
			return null;
		}
		logger.debug("album ({}) modified since: {}", id, sdf.format(albumCover.getLastUpdate()));
		return albumCover;
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public AlbumCover search(@RequestParam String name, WebRequest webRequest) {
		logger.debug("BEGIN {}", name);
		AlbumCover albumCover = albumCoverComp.getCoverByName(name);
		if (webRequest.checkNotModified(albumCover.getLastUpdate().getTime())) {
			return null;
		}
		logger.debug("album ({}) modified since: {}", name, sdf.format(albumCover.getLastUpdate()));
		return albumCover;
	}
}
