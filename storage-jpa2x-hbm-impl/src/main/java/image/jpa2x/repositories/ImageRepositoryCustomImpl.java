package image.jpa2x.repositories;

import image.cdm.image.ImageRating;
import image.cdm.image.status.ImageStatus;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entitytests.image.IImageFlagsUtils;
import image.persistence.entitytests.image.ImageFlags;
import image.persistence.entitytests.image.ImageMetadata;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

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
	public Image updateThumbLastModifiedForImg(Date thumbLastModified, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.getImageMetadata().setThumbLastModified(thumbLastModified);
		return image;
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
		ImageFlags imageFlags = of(imageStatus.getStatus());
		if (image.getFlags().equals(imageFlags)) {
			return false;
		}
		image.setFlags(imageFlags);
		image.getAlbum().setDirty(true);
		return true;
	}

	@Override
	public boolean markDeleted(Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		if (image.isDeleted()) {
			return false;
		}
		checkAndRemoveAlbumCoverAndFromAlbumImages(image, true);
		image.setDeleted(true);
		return true;
	}

	@Override
	public void safelyDeleteImage(Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		checkAndRemoveAlbumCoverAndFromAlbumImages(image, false);
	}

	@Override
	public void changeName(String name, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.setName(name);
	}

	@Override
	public Image updateImageMetadata(ImageMetadata imageMetadata, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.setImageMetadata(imageMetadata);
		return image;
	}

	/**
	 * Remove album's cover (set it to null) when image is its album's cover.
	 *
	 * @return whether change occurred or not in DB
	 */
	private void checkAndRemoveAlbumCoverAndFromAlbumImages(Image image, boolean onlyMarkAsDeleted) {
		Album album = image.getAlbum();
		if (!onlyMarkAsDeleted) {
			album.getImages().removeIf(i -> i.getId().equals(image.getId()));
		}
		if (album.getCover() == null) {
			// cover is already missing
			return;
		}
		if (!album.getCover().getId().equals(image.getId())) {
			// image is not cover for its album
			return;
		}
		// removing cover
		album.setCover(null);
	}
}
