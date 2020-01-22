package image.photos.infrastructure.database;

import image.persistence.entity.Image;

public interface AdvancedImageQueryRepository {
	/**
	 * this is the best approach when using 2nd level cache:
	 * take the imageId then load the Image
	 * <p>
	 * competes with ImageRepository.findByNameAndAlbumId
	 */
	Image findByNameAndAlbumId(String name, Integer albumId);
}
