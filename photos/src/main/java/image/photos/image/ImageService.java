package image.photos.image;

import image.persistence.entity.Image;

import java.util.List;

public interface ImageService {
	List<Image> getImages(Integer albumId);

	Image findByNameAndAlbumId(String name, Integer albumId);
}
