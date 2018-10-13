package image.exifweb.album;

import image.persistence.entity.Album;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

public interface AlbumCtrl {
	@RequestMapping(value = "/{id}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	Album getAlbumById(@PathVariable Integer id, WebRequest webRequest);

	@RequestMapping(value = "/byName/{name}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	Album findAlbumByName(@PathVariable String name, WebRequest webRequest);
}
