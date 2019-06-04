package image.photos.image;

import image.persistence.entity.Image;

import java.util.Optional;

public interface ImageService {
	Optional<Image> findByNameAndAlbumId(String name, Integer albumId);
}
