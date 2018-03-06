package image.photos.junit5.util.converter.factory;

import image.cdm.image.ExifInfo;
import image.persistence.entity.IExifInfoSupplier;
import image.persistence.entity.image.ExifData;
import image.photos.junit5.config.testconfig.Junit5InMemoryDbPhotosConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

@NotThreadSafe
@Junit5InMemoryDbPhotosConfig
@Tag("misc")
public class ExifInfoConverterTest implements IExifInfoSupplier {
	@Autowired
	private ConversionService cs;
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