package image.exifweb.album;

import com.fasterxml.jackson.annotation.JsonView;
import image.jpa2x.repositories.album.AlbumRepository;
import image.persistence.entity.Album;
import image.persistence.entity.jsonview.AlbumViews;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static image.persistence.entity.util.DateUtils.safeFormat;

/**
 * Created by adr on 2/9/18.
 */
@RestController
@RequestMapping("/json/album")
public class AlbumCtrlImpl implements AlbumCtrl {
	private static final Logger logger = LoggerFactory.getLogger(AlbumCtrlImpl.class);
	private static final DateTimeFormatter sdf =
			DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC);

	@Autowired
	private AlbumRepository albumRepository;

	@Override
	@RequestMapping(value = "/{id}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@JsonView(AlbumViews.Cover.class)
	public Album getAlbumById(@PathVariable Integer id, WebRequest webRequest) {
		logger.debug("BEGIN {}", id);
		Album album = this.albumRepository.getById(id);
		if (webRequest.checkNotModified(album.getLastUpdate().getTime())) {
			return null;
		}
		logger.debug("END album ({}) modified since: {}", id, safeFormat(album.getLastUpdate(), sdf));
		return album;
	}

	@Override
	@RequestMapping(value = "/byName/{name}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@JsonView(AlbumViews.Cover.class)
	public Album findAlbumByName(@PathVariable String name, WebRequest webRequest) {
		logger.debug("BEGIN {}", name);
		Album album = this.albumRepository.findByName(name);
		if (webRequest.checkNotModified(album.getLastUpdate().getTime())) {
			return null;
		}
		logger.debug("END album ({}) modified since: {}", name, safeFormat(album.getLastUpdate(), sdf));
		return album;
	}
}
