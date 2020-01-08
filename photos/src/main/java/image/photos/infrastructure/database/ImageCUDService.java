package image.photos.infrastructure.database;

import image.infrastructure.messaging.image.ImageEvent;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;
import reactor.util.function.Tuple2;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ImageCUDService {
	ImageEvent changeName(String newName, Integer imageId);

	ImageEvent safelyDeleteImage(Integer imageId);

	Optional<ImageEvent> markDeleted(Integer imageId);

	ImageEvent updateThumbLastModified(Date thumbLastModified, Integer imageId);

	List<ImageEvent> updateThumbLastModifiedMany(List<Tuple2<Date, Integer>> thumbLastModifiedForImageIds);

	ImageEvent updateImageMetadata(ImageMetadata imageMetadata, Integer imageId);

	List<ImageEvent> updateImageMetadataMany(List<Tuple2<ImageMetadata, Integer>> imageMetadataForImageIds);

	ImageEvent persist(Image image);

	List<ImageEvent> persistMany(List<Image> images);
}
