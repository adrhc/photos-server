package image.photostests.junit5.util.converter.entitytocdm;

import image.cdm.image.ExifInfo;
import image.persistence.entity.Image;
import image.persistence.entitytests.IImageSupplier;
import image.photostests.junit5.util.converter.ConversionTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Created by adr on 2/10/18.
 */
class ImageToExifInfoConverterTest extends ConversionTestBase implements IImageSupplier {
	private Image image;

	/**
	 * https://www.mkyong.com/hibernate/java-lang-classformaterror-absent-code-attribute-in-method-that-is-not-native-or-abstract-in-class-file/
	 */
	@BeforeAll
	void beforeAll() {
		this.image = supplyImage();
		// some versions of beanutils fail with Date null
		this.image.getImageMetadata().setDateTime(null);
	}

	@Test
	void convert() {
		ExifInfo exifInfo = this.cs.convert(this.image, ExifInfo.class);
		Assertions.assertAll(
				() -> Assertions.assertEquals(this.image.getName(), exifInfo.getName(), "name"),
				() -> Assertions.assertEquals(
						this.image.getImageMetadata().getDateTime(),
						exifInfo.getDateTime(), "dateTime"),
				() -> Assertions.assertEquals(
						this.image.getImageMetadata().getExifData().getDateTimeOriginal(),
						exifInfo.getDateTimeOriginal(), "dateTimeOriginal")
		);
	}
}
