package image.jpa2x.repositories;

import image.cdm.image.ImageRating;
import image.cdm.image.status.ImageStatus;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;

import java.util.Date;

/**
 * 4.6.1. Customizing Individual Repositories
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behavior
 */
public interface ImageRepositoryCustom {
	Image updateThumbLastModifiedForImg(Date thumbLastModified, Integer imageId);

	boolean changeRating(ImageRating imageRating);

	boolean changeStatus(ImageStatus imageStatus);

	boolean markDeleted(Integer imageId);

	void safelyDeleteImage(Integer imageId);

	void changeName(String name, Integer imageId);

	Image updateImageMetadata(ImageMetadata imageMetadata, Integer imageId);
}
