package image.persistence.entitytests.assertion;

import image.persistence.entity.Image;
import image.persistence.entity.image.ExifData;
import image.persistence.entity.image.ImageMetadata;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

	default void assertImageEquals(Image expected, Image actual) {
		assertAll("Image equality",
				() -> assertEquals(expected.getId(), actual.getId(), "id"),
				() -> assertEquals(expected.getName(), actual.getName(), "name"),
				() -> assertEquals(expected.getRating(), actual.getRating(), "rating"),
				() -> assertEquals(expected.isDeleted(), actual.isDeleted(), "deleted"),
				() -> assertEquals(expected.getFlags(), actual.getFlags(), "flags")
		);
		ImageMetadata imageMetadata = expected.getImageMetadata();
		ImageMetadata dbImageMetadata = actual.getImageMetadata();
		assertImageMetadataEquals(imageMetadata, dbImageMetadata);
		assertExifDataEquals(imageMetadata.getExifData(), dbImageMetadata.getExifData());
	}

	default void assertImagesEquals(List<Image> expected, List<Image> actual) {
		expected.forEach(img -> {
			Optional<Image> dbImgOpt = actual.stream()
					.filter(i -> i.getId().equals(img.getId()))
					.findAny();
			assertTrue(dbImgOpt.isPresent());
			assertImageEquals(img, dbImgOpt.get());
		});
	}
}
