package image.photostests.junit5.util.converter.cdmtoentity;

import image.cdm.image.ExifInfo;
import image.persistence.entity.image.ExifData;
import image.persistence.entitytests.IExifInfoSupplier;
import image.photostests.junit5.util.converter.ConversionTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ExifInfoCdmToEntityConverterTest
		extends ConversionTestBase implements IExifInfoSupplier {
	private ExifInfo exifInfo;

	@BeforeAll
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
