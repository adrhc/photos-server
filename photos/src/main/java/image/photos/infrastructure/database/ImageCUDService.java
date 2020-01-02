package image.photos.infrastructure.database;

import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;

import java.util.Date;

public interface ImageCUDService {
	void changeName(String newName, Integer imageId);

	void safelyDeleteImage(Integer imageId);

	void markDeleted(Integer imageId);

	void updateThumbLastModified(Date thumbLastModified, Integer imageId);

	void updateImageMetadata(ImageMetadata imageMetadata, Integer imageId);

	void persist(Image image);
}
