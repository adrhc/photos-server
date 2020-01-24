package image.jpa2x.repositories;

import image.cdm.image.ImageRating;
import image.cdm.image.status.ImageStatus;
import image.infrastructure.messaging.image.ImageEvent;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.IImageFlagsUtils;
import image.persistence.entity.image.ImageFlags;
import image.persistence.entity.image.ImageMetadata;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

import static image.infrastructure.messaging.image.ImageEventTypeEnum.UPDATED;

/**
 * 4.6.1. Customizing Individual Repositories
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behavior
 * The most important part of the class name that
 * corresponds to the fragment interface is the Impl postfix.
 */
@Transactional
public class ImageUpdateRepositoryImpl implements ImageUpdateRepository, IImageFlagsUtils {
	@PersistenceContext
	private EntityManager em;

	/**
	 * Remove album's cover (set it to null) when image is its album's cover.
	 *
	 * @return whether any change occurred in DB
	 */
	private static boolean removeAsCoverAndFromAlbumImages(Image persistentImage) {
		boolean result = false;
		Album album = persistentImage.getAlbum();
		// isDeleted means "labeled" as deleted
		if (!persistentImage.isDeleted()) {
			// purge image from DB
			result = album.getImages().remove(persistentImage);
		}
		if (album.getCover() == null ||
				!album.getCover().getId().equals(persistentImage.getId())) {
			// album has no cover or image is not the cover for its album
			return result;
		}
		// removing cover
		album.setCover(null);
		return true;
	}

	@Override
	public void updateImageMetadata(ImageMetadata imageMetadata, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.setImageMetadata(imageMetadata);
	}

	@Override
	public void updateThumbLastModified(Date thumbLastModified, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.getImageMetadata().setThumbLastModified(thumbLastModified);
	}

	@Override
	public ImageEvent changeName(String newName, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.setName(newName);
		return ImageEvent.of(image, UPDATED);
	}

	@Override
	public boolean markDeleted(Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		if (image.isDeleted()) {
			return false;
		}
		image.setDeleted(true);
		removeAsCoverAndFromAlbumImages(image);
		return true;
	}

	@Override
	public boolean safelyDeleteImage(Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		return removeAsCoverAndFromAlbumImages(image);
	}

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
		ImageFlags imageFlags = this.of(imageStatus.getStatus());
		if (image.getFlags().equals(imageFlags)) {
			return false;
		}
		image.setFlags(imageFlags);
		image.getAlbum().setDirty(true);
		return true;
	}
}
