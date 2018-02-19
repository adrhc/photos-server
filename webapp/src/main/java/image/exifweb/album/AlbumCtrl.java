package image.exifweb.album;

import image.persistence.entity.Album;
import image.persistence.repository.AlbumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;
import java.text.SimpleDateFormat;

/**
 * Created by adr on 2/9/18.
 */
@RestController
@RequestMapping("/json/album")
public class AlbumCtrl {
	private static final Logger logger = LoggerFactory.getLogger(AlbumCtrl.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");

	@Inject
	private AlbumRepository albumRepository;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
