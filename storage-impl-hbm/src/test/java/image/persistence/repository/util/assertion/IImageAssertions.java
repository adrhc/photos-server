package image.persistence.repository.util.assertion;

import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface IImageAssertions {
	default void assertImageEquals(Image image, Image dbImage) {
		ImageMetadata imageMetadata = image.getImageMetadata();
		ImageMetadata dbImageMetadata = dbImage.getImageMetadata();
		assertAll("equals images",
				() -> assertEquals(image.getId(), dbImage.getId(), "id"),
				() -> assertEquals(image.getName(), dbImage.getName(), "name"),
				() -> assertEquals(image.getRating(), dbImage.getRating(), "rating"),
				() -> assertEquals(image.getStatus(), dbImage.getStatus(), "status"),
				() -> assertEquals(imageMetadata.getDateTime(),
						dbImageMetadata.getDateTime(), "dateTime"),
				() -> assertEquals(imageMetadata.getThumbLastModified(),
						dbImageMetadata.getThumbLastModified(), "thumbLastModified")
		);
	}
}
