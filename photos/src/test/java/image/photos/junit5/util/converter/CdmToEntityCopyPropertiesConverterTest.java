package image.photos.junit5.util.converter;

import image.cdm.AppConfig;
import image.cdm.image.ExifInfo;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.entity.IExifInfoSupplier;
import image.persistence.entity.image.ExifData;
import image.photos.junit5.config.IAppConfigAssertions;
import image.photos.util.converter.CdmToEntityCopyPropertiesConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;

class CdmToEntityCopyPropertiesConverterTest implements
		IExifInfoSupplier, IAppConfigSupplier, IAppConfigAssertions {
	CdmToEntityCopyPropertiesConverter converter =
			new CdmToEntityCopyPropertiesConverter();

	private ExifInfo exifInfo;
	private AppConfig appConfig;

	@BeforeEach
	void setUp() {
		this.exifInfo = supplyExifInfo();
		// some versions of beanutils fail with Date null
		this.exifInfo.setDateTime(null);

		this.appConfig = supplyCdmAppConfig();
	}

	@Test
	void convert() {
		ExifData exifData = (ExifData) this.converter.convert(this.exifInfo);
		image.persistence.entity.AppConfig appConfig = (image.persistence.entity.AppConfig)
				this.converter.convert(this.appConfig);
		Assertions.assertAll("convert",
				() -> Assertions.assertEquals(this.exifInfo.getDateTimeOriginal(),
						exifData.getDateTimeOriginal(), "exifInfo"),
				() -> assertAppConfigEquals("appConfig", this.appConfig, appConfig)
		);
	}

	@Test
	void matches() {
		Assertions.assertAll("matches",
				() -> Assertions.assertTrue(this.converter.matches(
						TypeDescriptor.valueOf(AppConfig.class),
						TypeDescriptor.valueOf(image.persistence.entity.AppConfig.class)), "AppConfig"),
				() -> Assertions.assertTrue(this.converter.matches(
						TypeDescriptor.valueOf(ExifInfo.class),
						TypeDescriptor.valueOf(ExifData.class)), "ExifInfo - ExifData"),
				() -> Assertions.assertFalse(this.converter.matches(
						TypeDescriptor.valueOf(ExifData.class),
						TypeDescriptor.valueOf(ExifData.class)), "ExifData - ExifData"),
				() -> Assertions.assertFalse(this.converter.matches(
						TypeDescriptor.valueOf(ExifInfo.class),
						TypeDescriptor.valueOf(ExifInfo.class)), "ExifInfo - ExifInfo")
		);
	}
}