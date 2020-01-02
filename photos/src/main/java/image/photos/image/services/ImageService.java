package image.photos.image.services;

import image.persistence.entity.Image;

import java.util.List;

public interface ImageService {
	List<Image> getImages(Integer albumId);

	Image findByNameAndAlbumId(String name, Integer albumId);
}
