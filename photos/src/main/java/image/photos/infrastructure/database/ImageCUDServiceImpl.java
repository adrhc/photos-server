package image.photos.infrastructure.database;

import image.infrastructure.messaging.image.ImageEvent;
import image.infrastructure.messaging.image.ImageEventTypeEnum;
import image.jpa2x.repositories.ImageRepository;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static image.infrastructure.messaging.image.ImageEvent.of;
import static image.infrastructure.messaging.image.ImageEventTypeEnum.*;

/**
 * Using 4.6.1. Customizing Individual Repositories:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.single-repository-behavior
 * pose the risk of someone using the method from the
 * Repository instead of the one emitting the event.
 */
@Service
@Slf4j
public class ImageCUDServiceImpl implements ImageCUDService {
	@Autowired
	private ImageRepository imageRepository;

	@Override
	public ImageEvent changeName(String newName, Integer imageId) {
		// transaction
		this.imageRepository.changeName(newName, imageId);
		Image image = this.imageRepository.getById(imageId);
		// emission (only when transaction succeeds)
		return of(image, ImageEventTypeEnum.UPDATED);
	}

	@Override
	public ImageEvent safelyDeleteImage(Integer imageId) {
		// transaction
		Image image = this.imageRepository.getById(imageId);
		boolean deleted = this.imageRepository.safelyDeleteImage(imageId);
		// emission (only when transaction succeeds)
		return of(image, deleted ? DELETED : NOTHING);
	}

	@Override
	public ImageEvent markDeleted(Integer imageId) {
		// transaction
		boolean deleted = this.imageRepository.markDeleted(imageId);
		Image image = this.imageRepository.getById(imageId);
		// emission (only when transaction succeeds)
		return of(image, deleted ? MARKED_AS_DELETED : NOTHING);
	}

	@Override
	public ImageEvent updateThumbLastModified(Date thumbLastModified, Integer imageId) {
		// transaction
		this.imageRepository.updateThumbLastModified(thumbLastModified, imageId);
		Image image = this.imageRepository.getById(imageId);
		// emission (only when transaction succeeds)
		log.debug("updated thumb's lastModified for {}", image.getName());
		return of(image, THUMB_LAST_MODIF_DATE_UPDATED);
	}

	@Override
	public ImageEvent updateImageMetadata(ImageMetadata imageMetadata, Integer imageId) {
		// transaction
		this.imageRepository.updateImageMetadata(imageMetadata, imageId);
		Image image = this.imageRepository.getById(imageId);
		// emission (only when transaction succeeds)
		return of(image, EXIF_UPDATED);
	}

	/**
	 * todo: what if an Album with Image(s) is persisted; the related events won't be emitted
	 */
	@Override
	public ImageEvent persist(Image image) {
		// transaction
		this.imageRepository.persist(image);
		// emission (only when transaction succeeds)
		return of(image, CREATED);
	}
}
