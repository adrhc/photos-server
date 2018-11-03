package image.persistence.entitytests.assertion;

import image.persistence.entity.Image;
import image.persistence.entitytests.image.ExifData;
import image.persistence.entitytests.image.ImageMetadata;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface IImageAssertions {
	default void assertExifDataEquals(ExifData exifData, ExifData dbExifData) {
		assertAll("ExifData equality",
				() -> assertEquals(exifData.getApertureValue(),
						dbExifData.getApertureValue(), "apertureValue"),
				() -> assertEquals(exifData.getContrast(),
						dbExifData.getContrast(), "contrast"),
				() -> assertEquals(exifData.getDateTimeOriginal(),
						dbExifData.getDateTimeOriginal(), "dateTimeOriginal"),
				() -> assertEquals(exifData.getfNumber(),
						dbExifData.getfNumber(), "fNumber"),
				() -> assertEquals(exifData.getImageHeight(),
						dbExifData.getImageHeight(), "imageHeight"),
				() -> assertEquals(exifData.getWhiteBalanceMode(),
						dbExifData.getWhiteBalanceMode(), "whiteBalanceMode")
		);
	}

	default void assertImageMetadataEquals(ImageMetadata imageMetadata, ImageMetadata dbImageMetadata) {
		assertAll("ImageMetadata equality",
				() -> assertEquals(imageMetadata.getDateTime(),
						dbImageMetadata.getDateTime(), "dateTime"),
				() -> assertEquals(imageMetadata.getThumbLastModified(),
						dbImageMetadata.getThumbLastModified(), "thumbLastModified")
		);
	}

	default void assertImageEquals(Image image, Image dbImage) {
		assertAll("Image equality",
				() -> assertEquals(image.getId(), dbImage.getId(), "id"),
				() -> assertEquals(image.getName(), dbImage.getName(), "name"),
				() -> assertEquals(image.getRating(), dbImage.getRating(), "rating"),
				() -> assertEquals(image.isDeleted(), dbImage.isDeleted(), "deleted"),
				() -> assertEquals(image.getFlags(), dbImage.getFlags(), "flags")
		);
		ImageMetadata imageMetadata = image.getImageMetadata();
		ImageMetadata dbImageMetadata = dbImage.getImageMetadata();
		assertImageMetadataEquals(imageMetadata, dbImageMetadata);
		assertExifDataEquals(imageMetadata.getExifData(), dbImageMetadata.getExifData());
	}
}
