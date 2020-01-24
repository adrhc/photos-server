package image.jpa2x.repositories.image;

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

import static image.infrastructure.messaging.image.ImageEventTypeEnum.*;

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
	public ImageEvent updateImageMetadata(ImageMetadata imageMetadata, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.setImageMetadata(imageMetadata);
		return ImageEvent.of(image, EXIF_UPDATED);
	}

	@Override
	public ImageEvent updateThumbLastModified(Date thumbLastModified, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.getImageMetadata().setThumbLastModified(thumbLastModified);
		return ImageEvent.of(image, THUMB_LAST_MODIF_DATE_UPDATED);
	}

	@Override
	public ImageEvent changeName(String newName, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.setName(newName);
		return ImageEvent.of(image, UPDATED);
	}

	@Override
	public ImageEvent markDeleted(Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		if (image.isDeleted()) {
			return ImageEvent.of(image, NOTHING);
		}
		image.setDeleted(true);
		removeAsCoverAndFromAlbumImages(image);
		return ImageEvent.of(image, MARKED_AS_DELETED);
	}

	@Override
	public ImageEvent safelyDeleteImage(Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		boolean deleted = removeAsCoverAndFromAlbumImages(image);
		return ImageEvent.of(image, deleted ? DELETED : NOTHING);
	}

	@Override
	public ImageEvent changeRating(ImageRating imageRating) {
		Image image = this.em.find(Image.class, imageRating.getImageId());
		if (image.getRating() == imageRating.getRating()) {
//			logger.debug("END (same rating {})", imageRating.getRating());
			return ImageEvent.of(image, NOTHING);
		}
//		logger.debug("before setRating({})", imageRating.getRating());
		image.setRating(imageRating.getRating());
//		logger.debug("before album.setDirty(true), {}",
//				sdf.format(image.getAlbum().getLastUpdate()));
		image.getAlbum().setDirty(true);
//		logger.debug("END");
		return ImageEvent.of(image, RATING_CHANGED);
	}

	@Override
	public ImageEvent changeStatus(ImageStatus imageStatus) {
		Image image = this.em.find(Image.class, imageStatus.getImageId());
		ImageFlags imageFlags = this.of(imageStatus.getStatus());
		if (image.getFlags().equals(imageFlags)) {
			return ImageEvent.of(image, NOTHING);
		}
		image.setFlags(imageFlags);
		image.getAlbum().setDirty(true);
		return ImageEvent.of(image, STATUS_CHANGED);
	}

	public ImageEvent insert(Image image) {
		this.em.persist(image);
		return ImageEvent.of(image, CREATED);
	}
}
