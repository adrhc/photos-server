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

	@Override
	public ImageEvent updateImageMetadata(ImageMetadata imageMetadata, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.setImageMetadata(imageMetadata);
		image.getAlbum().setDirty(true);
		return ImageEvent.of(image, EXIF_UPDATED);
	}

	@Override
	public ImageEvent updateThumbLastModified(Date thumbLastModified, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.getImageMetadata().setThumbLastModified(thumbLastModified);
		image.getAlbum().setDirty(true);
		return ImageEvent.of(image, THUMB_LAST_MODIF_DATE_UPDATED);
	}

	@Override
	public ImageEvent changeName(String newName, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.setName(newName);
		image.getAlbum().setDirty(true);
		return ImageEvent.of(image, UPDATED);
	}

	@Override
	public ImageEvent markDeleted(Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		if (image.isDeleted()) {
			return ImageEvent.of(image, NOTHING);
		}
		image.setDeleted(true);
		this.removeAsCoverAndFromAlbumImages(image);
		return ImageEvent.of(image, MARKED_AS_DELETED);
	}

	@Override
	public ImageEvent safelyDeleteImage(Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		boolean deleted = this.removeAsCoverAndFromAlbumImages(image);
		return ImageEvent.of(image, DELETED, !deleted);
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
		image.getAlbum().setDirty(true);
		return ImageEvent.of(image, CREATED);
	}

	/**
	 * Remove album's cover (set it to null) when image is its album's cover.
	 *
	 * @return whether any change occurred in DB
	 */
	private boolean removeAsCoverAndFromAlbumImages(Image persistentImage) {
		boolean result = false;
		Album album = persistentImage.getAlbum();
		// isDeleted means "labeled" as deleted
		if (!persistentImage.isDeleted()) {
			// purges the image from DB
/*
			// loads all images from DB!
			result = album.getImages().remove(persistentImage);
			if (result) {
				album.setDirty(true);
			}
*/
			this.em.remove(persistentImage);
			album.setDirty(true);
			result = true;
		}
		if (album.getCover() == null ||
				!album.getCover().getId().equals(persistentImage.getId())) {
			// album has no cover or image is not the cover for its album
			return result;
		}
		// removing cover
		album.setCover(null);
		album.setDirty(true);
		return true;
	}
}
