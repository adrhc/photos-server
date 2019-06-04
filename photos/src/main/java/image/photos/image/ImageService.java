package image.photos.image;

import image.persistence.entity.Image;

public interface ImageService {
	Image findByNameAndAlbumId(String name, Integer albumId);
}
