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
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
		Image eventData = this.transact.write(em -> {
			Image image = em.find(Image.class, imageId);
			image.setName(newName);
			return image;
		});
		// emission (only when transaction succeeds)
		return of(eventData, ImageEventTypeEnum.UPDATED);
	}

	@Override
	public ImageEvent safelyDeleteImage(Integer imageId) {
		// transaction
		Image eventData = this.transact.write(em -> {
			Image image = em.find(Image.class, imageId);
			removeAsCoverAndFromAlbumImages(image);
			return image;
		});
		// emission (only when transaction succeeds)
		return of(eventData, DELETED);
	}

	@Override
	public Optional<ImageEvent> markDeleted(Integer imageId) {
		// transaction
		Image eventData = this.transact.write(em -> {
			Image image = em.find(Image.class, imageId);
			if (image.isDeleted()) {
				return null;
			}
			image.setDeleted(true);
			removeAsCoverAndFromAlbumImages(image);
			return image;
		});
		// emission (only when transaction succeeds)
		return Optional.ofNullable(eventData != null ? of(eventData, MARKED_AS_DELETED) : null);
	}

	@Override
	@Transactional
	public ImageEvent updateThumbLastModified(Date thumbLastModified, Integer imageId) {
		// transaction
		Image eventData = this.transact.write(em -> {
			Image image = em.find(Image.class, imageId);
			image.getImageMetadata().setThumbLastModified(thumbLastModified);
			return image;
		});
		// emission (only when transaction succeeds)
		log.debug("updated thumb's lastModified for {}", eventData.getName());
		return of(eventData, THUMB_LAST_MODIF_DATE_UPDATED);
	}

	public List<ImageEvent> updateThumbLastModifiedMany(List<Tuple2<Date, Integer>> thumbLastModifiedForImageIds) {
		List<Image> images = new ArrayList<>(thumbLastModifiedForImageIds.size());
		// transaction
		this.transact.writeWithVoidResult(em ->
				thumbLastModifiedForImageIds.forEach(t2 -> {
					Image image = em.find(Image.class, t2.getT2());
					image.getImageMetadata().setThumbLastModified(t2.getT1());
					images.add(image);
				}));
		// emission (only when transaction succeeds)
		log.debug("updated thumb's lastModified for {} images", thumbLastModifiedForImageIds.size());
		return of(images, THUMB_LAST_MODIF_DATE_UPDATED);
	}

	@Override
	public ImageEvent updateImageMetadata(ImageMetadata imageMetadata, Integer imageId) {
		// transaction
		Image eventData = this.transact.write(em -> {
			Image image = em.find(Image.class, imageId);
			image.setImageMetadata(imageMetadata);
			return image;
		});
		// emission (only when transaction succeeds)
		return of(eventData, EXIF_UPDATED);
	}

	public List<ImageEvent> updateImageMetadataMany(List<Tuple2<ImageMetadata, Integer>> imageMetadataForImageIds) {
		List<Image> images = new ArrayList<>(imageMetadataForImageIds.size());
		// transaction
		this.transact.writeWithVoidResult(em ->
				imageMetadataForImageIds.forEach(t2 -> {
					Image image = em.find(Image.class, t2.getT2());
					image.setImageMetadata(t2.getT1());
					images.add(image);
				}));
		// emission (only when transaction succeeds)
		log.debug("updated thumb's ImageMetadata for {} images", imageMetadataForImageIds.size());
		return of(images, EXIF_UPDATED);
	}


	/**
	 * todo: what if an Album with Image(s) is persisted; the related events won't be emitted
	 */
	@Override
	public ImageEvent persist(Image image) {
		// transaction
		Image eventData = this.transact.write(em -> {
			em.persist(image);
			return image;
		});
		// emission (only when transaction succeeds)
		return of(eventData, CREATED);
	}

	@Override
	public List<ImageEvent> persistMany(List<Image> images) {
		// transaction
		this.transact.writeWithVoidResult(em -> images.forEach(em::persist));
		// emission (only when transaction succeeds)
		return images.stream().map(i -> of(i, CREATED)).collect(Collectors.toList());
	}
}
