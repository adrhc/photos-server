package image.exifweb.album;

import image.persistence.entity.Album;
import org.springframework.web.context.request.WebRequest;

public interface AlbumCtrl {
	Album getAlbumById(Integer id, WebRequest webRequest);

	Album findAlbumByName(String name, WebRequest webRequest);
}
