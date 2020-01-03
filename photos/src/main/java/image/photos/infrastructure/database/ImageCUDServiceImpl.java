package image.photos.infrastructure.database;

import image.infrastructure.messaging.image.ImageEventTypeEnum;
import image.infrastructure.messaging.image.ImageTopic;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

import static image.infrastructure.messaging.image.ImageEvent.of;
import static image.infrastructure.messaging.image.ImageEventTypeEnum.*;

/**
 * Using TransactionalOperation assures us that the database operations
 * complete before emitting an event (e.g. imageTopic.emit).
 * <p>
 * Alternatively we could use 4.6.1. Customizing Individual Repositories:
 * * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behavior
 * with the risk of someone using the method from the
 * Repository instead of the one emitting the event.
 */
@Service
@Slf4j
public class ImageCUDServiceImpl implements ImageCUDService {
	private final ImageTopic imageTopic;
	@Autowired
	private TransactionalOperation transact;
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
		// transaction
		Image eventData = transact.readWrite(() -> {
			Image image = this.em.find(Image.class, imageId);
			image.setName(newName);
			return image;
		});
		// emission
		this.imageTopic.emit(of(eventData, ImageEventTypeEnum.UPDATED));
	}

	@Override
	public void safelyDeleteImage(Integer imageId) {
		// transaction
		Image eventData = transact.readWrite(() -> {
			Image image = this.em.find(Image.class, imageId);
			removeAsCoverAndFromAlbumImages(image);
			return image;
		});
		// emission
		this.imageTopic.emit(of(eventData, DELETED));
	}

	@Override
	public void markDeleted(Integer imageId) {
		// transaction
		Image eventData = transact.readWrite(() -> {
			Image image = this.em.find(Image.class, imageId);
			if (image.isDeleted()) {
				return null;
			}
			image.setDeleted(true);
			removeAsCoverAndFromAlbumImages(image);
			return image;
		});
		// emission
		if (eventData != null) {
			this.imageTopic.emit(of(eventData, MARKED_AS_DELETED));
		}
	}

	@Override
	@Transactional
	public void updateThumbLastModified(Date thumbLastModified, Integer imageId) {
		// transaction
		Image eventData = transact.readWrite(() -> {
			Image image = this.em.find(Image.class, imageId);
			image.getImageMetadata().setThumbLastModified(thumbLastModified);
			return image;
		});
		// emission
		log.debug("updated thumb's lastModified for {}", eventData.getName());
		this.imageTopic.emit(of(eventData, THUMB_LAST_MODIF_DATE_UPDATED));
	}

	@Override
	public void updateImageMetadata(ImageMetadata imageMetadata, Integer imageId) {
		// transaction
		Image eventData = transact.readWrite(() -> {
			Image image = this.em.find(Image.class, imageId);
			image.setImageMetadata(imageMetadata);
			return image;
		});
		// emission
		this.imageTopic.emit(of(eventData, EXIF_UPDATED));
	}

	/**
	 * todo: what if an Album with Image(s) is persisted; the related events won't be emitted
	 */
	@Override
	public void persist(Image image) {
		// transaction
		this.em.persist(image);
		// emission
		this.imageTopic.emit(of(image, CREATED));
	}
}
