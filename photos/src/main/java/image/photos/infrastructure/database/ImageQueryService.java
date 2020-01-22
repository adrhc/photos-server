package image.photos.infrastructure.database;

import image.persistence.entity.Image;

import java.io.IOException;
import java.nio.file.Path;

public interface ImageQueryService {
	Image findByNameAndAlbumId(String name, Integer albumId);

	boolean imageExistsInOtherAlbum(Path imgFile, Integer albumId) throws IOException;
}
