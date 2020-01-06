package image.photos.infrastructure.database;

import image.infrastructure.messaging.image.ImageEvent;
import image.infrastructure.messaging.image.ImageEventTypeEnum;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

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
	@Autowired
	private TransactionalOperation transact;

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
	public ImageEvent changeName(String newName, Integer imageId) {
		// transaction
		Image eventData = transact.write(em -> {
			Image image = em.find(Image.class, imageId);
			image.setName(newName);
			return image;
		});
		// emission
		return of(eventData, ImageEventTypeEnum.UPDATED);
	}

	@Override
	public ImageEvent safelyDeleteImage(Integer imageId) {
		// transaction
		Image eventData = transact.write(em -> {
			Image image = em.find(Image.class, imageId);
			removeAsCoverAndFromAlbumImages(image);
			return image;
		});
		// emission
		return of(eventData, DELETED);
	}

	@Override
	public Optional<ImageEvent> markDeleted(Integer imageId) {
		// transaction
		Image eventData = transact.write(em -> {
			Image image = em.find(Image.class, imageId);
			if (image.isDeleted()) {
				return null;
			}
			image.setDeleted(true);
			removeAsCoverAndFromAlbumImages(image);
			return image;
		});
		// emission
		return Optional.ofNullable(eventData != null ? of(eventData, MARKED_AS_DELETED) : null);
	}

	@Override
	@Transactional
	public ImageEvent updateThumbLastModified(Date thumbLastModified, Integer imageId) {
		// transaction
		Image eventData = transact.write(em -> {
			Image image = em.find(Image.class, imageId);
			image.getImageMetadata().setThumbLastModified(thumbLastModified);
			return image;
		});
		// emission
		log.debug("updated thumb's lastModified for {}", eventData.getName());
		return of(eventData, THUMB_LAST_MODIF_DATE_UPDATED);
	}

	@Override
	public ImageEvent updateImageMetadata(ImageMetadata imageMetadata, Integer imageId) {
		// transaction
		Image eventData = transact.write(em -> {
			Image image = em.find(Image.class, imageId);
			image.setImageMetadata(imageMetadata);
			return image;
		});
		// emission
		return of(eventData, EXIF_UPDATED);
	}

	/**
	 * todo: what if an Album with Image(s) is persisted; the related events won't be emitted
	 */
	@Override
	public ImageEvent persist(Image image) {
		// transaction
		Image eventData = transact.write(em -> {
			em.persist(image);
			return image;
		});
		// emission
		return of(eventData, CREATED);
	}
}
