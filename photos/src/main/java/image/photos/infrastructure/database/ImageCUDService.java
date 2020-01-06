package image.photos.infrastructure.database;

import image.infrastructure.messaging.image.ImageEvent;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;

import java.util.Date;
import java.util.Optional;

public interface ImageCUDService {
	ImageEvent changeName(String newName, Integer imageId);

	ImageEvent safelyDeleteImage(Integer imageId);

	Optional<ImageEvent> markDeleted(Integer imageId);

	ImageEvent updateThumbLastModified(Date thumbLastModified, Integer imageId);

	ImageEvent updateImageMetadata(ImageMetadata imageMetadata, Integer imageId);

	ImageEvent persist(Image image);
}
