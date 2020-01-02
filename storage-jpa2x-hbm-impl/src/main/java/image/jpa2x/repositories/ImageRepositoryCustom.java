package image.jpa2x.repositories;

import image.cdm.image.ImageRating;
import image.cdm.image.status.ImageStatus;

/**
 * 4.6.1. Customizing Individual Repositories
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behavior
 */
public interface ImageRepositoryCustom {
	boolean changeRating(ImageRating imageRating);

	boolean changeStatus(ImageStatus imageStatus);
}
