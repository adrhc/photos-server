package image.persistence.repository;

import image.cdm.image.ImageRating;
import image.cdm.image.status.ImageStatus;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;

import java.util.Date;
import java.util.List;

/**
 * Created by adrianpetre on 23.02.2018.
 */
public interface ImageRepository {
	Image updateThumbLastModifiedForImg(Date thumbLastModified, Integer imageId);

	boolean changeRating(ImageRating imageRating);

	boolean changeStatus(ImageStatus imageStatus);

	boolean markDeleted(Integer imageId);

	void safelyDeleteImage(Integer imageId);

	void changeName(String name, Integer imageId);

	Image updateImageMetadata(ImageMetadata imageMetadata, Integer imageId);

	Image findByNameAndAlbumId(String name, Integer albumId);

	void deleteById(Integer imageId);

	Image getById(Integer imageId);

	List<Image> findByAlbumId(Integer albumId);

	void persist(Image image);
}
