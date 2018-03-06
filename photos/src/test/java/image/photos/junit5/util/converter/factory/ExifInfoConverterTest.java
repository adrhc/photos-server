package image.photos.junit5.util.converter.factory;

import image.cdm.image.ExifInfo;
import image.persistence.entity.IExifInfoSupplier;
import image.persistence.entity.image.ExifData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExifInfoConverterTest
		extends ConverterFactoryTestBase implements IExifInfoSupplier {
	private ExifInfo exifInfo;

	@BeforeEach
	void setUp() {
		this.exifInfo = supplyExifInfo();
		// some versions of beanutils fail with Date null
		this.exifInfo.setDateTime(null);
	}

	@Test
	void convert() {
		ExifData exifData = this.cs.convert(this.exifInfo, ExifData.class);
		Assertions.assertEquals(this.exifInfo.getDateTimeOriginal(),
				exifData.getDateTimeOriginal(), "exifInfo");
	}
}