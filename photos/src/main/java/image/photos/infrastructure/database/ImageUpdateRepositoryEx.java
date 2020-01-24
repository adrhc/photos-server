package image.photos.infrastructure.database;

import image.infrastructure.messaging.image.ImageEvent;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;

import java.util.Date;

public interface ImageUpdateRepositoryEx {
	ImageEvent safelyDeleteImage(Integer imageId);

	ImageEvent markDeleted(Integer imageId);

	ImageEvent updateThumbLastModified(Date thumbLastModified, Integer imageId);

	ImageEvent updateImageMetadata(ImageMetadata imageMetadata, Integer imageId);

	ImageEvent persist(Image image);
}
