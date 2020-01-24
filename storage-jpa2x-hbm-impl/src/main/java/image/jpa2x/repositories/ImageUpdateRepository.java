package image.jpa2x.repositories;

import image.cdm.image.ImageRating;
import image.cdm.image.status.ImageStatus;
import image.persistence.entity.image.ImageMetadata;

import java.util.Date;

/**
 * 4.6.1. Customizing Individual Repositories
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behavior
 */
public interface ImageUpdateRepository {
	boolean changeRating(ImageRating imageRating);

	boolean changeStatus(ImageStatus imageStatus);

	boolean safelyDeleteImage(Integer imageId);

	boolean markDeleted(Integer imageId);

	void changeName(String newName, Integer imageId);

	void updateThumbLastModified(Date thumbLastModified, Integer imageId);

	void updateImageMetadata(ImageMetadata imageMetadata, Integer imageId);
}
