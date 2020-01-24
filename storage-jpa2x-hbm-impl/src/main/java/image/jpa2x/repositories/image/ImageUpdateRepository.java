package image.jpa2x.repositories.image;

import image.cdm.image.ImageRating;
import image.cdm.image.status.ImageStatus;
import image.infrastructure.messaging.image.ImageEvent;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;

import java.util.Date;

/**
 * 4.6.1. Customizing Individual Repositories
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behavior
 */
public interface ImageUpdateRepository {
	ImageEvent changeRating(ImageRating imageRating);

	ImageEvent changeStatus(ImageStatus imageStatus);

	ImageEvent safelyDeleteImage(Integer imageId);

	ImageEvent markDeleted(Integer imageId);

	ImageEvent changeName(String newName, Integer imageId);

	ImageEvent updateThumbLastModified(Date thumbLastModified, Integer imageId);

	ImageEvent updateImageMetadata(ImageMetadata imageMetadata, Integer imageId);

	ImageEvent insert(Image image);
}
