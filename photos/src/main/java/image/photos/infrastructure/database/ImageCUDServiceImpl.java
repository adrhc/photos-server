package image.photos.infrastructure.database;

import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;
import image.infrastructure.messaging.image.ImageEventTypeEnum;
import image.infrastructure.messaging.image.ImageTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

import static image.infrastructure.messaging.image.ImageEvent.of;
import static image.infrastructure.messaging.image.ImageEventTypeEnum.*;

@Transactional
@Service
@Slf4j
public class ImageCUDServiceImpl implements ImageCUDService {
	private final ImageTopic imageTopic;
	@PersistenceContext
	private EntityManager em;

	public ImageCUDServiceImpl(ImageTopic imageTopic) {
		this.imageTopic = imageTopic;
	}

	/**
	 * Remove album's cover (set it to null) when image is its album's cover.
	 *
	 * @return whether change occurred or not in DB
	 */
	private static void removeAsCoverAndFromAlbumImages(Image persistentImage) {
		Album album = persistentImage.getAlbum();
		// isDeleted means "marked as deleted"
		if (!persistentImage.isDeleted()) {
			album.getImages().remove(persistentImage);
		}
		if (album.getCover() == null ||
				!album.getCover().getId().equals(persistentImage.getId())) {
			// album has no cover or image is not the cover for its album
			return;
		}
		// removing cover
		album.setCover(null);
	}

	@Override
	public void changeName(String newName, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.setName(newName);
		this.imageTopic.emit(of(image, ImageEventTypeEnum.UPDATED));
	}

	@Override
	public void safelyDeleteImage(Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		removeAsCoverAndFromAlbumImages(image);
		this.imageTopic.emit(of(image, DELETED));
	}

	@Override
	public void markDeleted(Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		if (image.isDeleted()) {
			return;
		}
		image.setDeleted(true);
		removeAsCoverAndFromAlbumImages(image);
		this.imageTopic.emit(of(image, MARKED_DELETED));
	}

	@Override
	public void updateThumbLastModified(Date thumbLastModified, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.getImageMetadata().setThumbLastModified(thumbLastModified);
		log.debug("updated thumb's lastModified for {}", image.getName());
		this.imageTopic.emit(of(image, THUMB_LAST_MODIF_DATE_UPDATED));
	}

	@Override
	public void updateImageMetadata(ImageMetadata imageMetadata, Integer imageId) {
		Image image = this.em.find(Image.class, imageId);
		image.setImageMetadata(imageMetadata);
		this.imageTopic.emit(of(image, EXIF_UPDATED));
	}

	@Override
	public void persist(Image image) {
		this.em.persist(image);
		this.imageTopic.emit(of(image, CREATED));
	}
}
