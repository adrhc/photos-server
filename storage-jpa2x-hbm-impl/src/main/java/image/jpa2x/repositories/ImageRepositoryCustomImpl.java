package image.jpa2x.repositories;

import image.cdm.image.ImageRating;
import image.cdm.image.status.ImageStatus;
import image.persistence.entity.Image;
import image.persistence.entity.image.IImageFlagsUtils;
import image.persistence.entity.image.ImageFlags;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 4.6.1. Customizing Individual Repositories
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behavior
 * The most important part of the class name that corresponds to the fragment interface is the Impl postfix.
 */
@Transactional
public class ImageRepositoryCustomImpl implements ImageRepositoryCustom, IImageFlagsUtils {
	@PersistenceContext
	private EntityManager em;

	@Override
	public boolean changeRating(ImageRating imageRating) {
		Image image = this.em.find(Image.class, imageRating.getImageId());
		if (image.getRating() == imageRating.getRating()) {
//			logger.debug("END (same rating {})", imageRating.getRating());
			return false;
		}
//		logger.debug("before setRating({})", imageRating.getRating());
		image.setRating(imageRating.getRating());
//		logger.debug("before album.setDirty(true), {}",
//				sdf.format(image.getAlbum().getLastUpdate()));
		image.getAlbum().setDirty(true);
//		logger.debug("END");
		return true;
	}

	@Override
	public boolean changeStatus(ImageStatus imageStatus) {
		Image image = this.em.find(Image.class, imageStatus.getImageId());
		ImageFlags imageFlags = of(imageStatus.getStatus());
		if (image.getFlags().equals(imageFlags)) {
			return false;
		}
		image.setFlags(imageFlags);
		image.getAlbum().setDirty(true);
		return true;
	}
}
