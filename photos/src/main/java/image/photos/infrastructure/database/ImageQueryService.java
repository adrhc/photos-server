package image.photos.infrastructure.database;

import image.persistence.entity.Image;

import java.nio.file.Path;
import java.util.List;

public interface ImageQueryService {
	List<Image> getImages(Integer albumId);

	Image findByNameAndAlbumId(String name, Integer albumId);

	boolean imageExistsInOtherAlbum(Path imgFile, Integer albumId);
}
