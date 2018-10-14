package image.photos.junit5.util.converter.entitytocdm;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.photos.junit5.util.assertion.IAppConfigAssertions;
import image.photos.junit5.util.converter.ConversionTestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AppConfigEntityToCdmConverterTest
		extends ConversionTestBase
		implements IAppConfigSupplier, IAppConfigAssertions {
	private AppConfig source;

	@BeforeAll
	void beforeAll() {
		this.source = randomInstance(true, AppConfig.class);
	}

	@Test
	public void convert() {
		image.cdm.AppConfig appConfig = this.cs.convert(this.source, image.cdm.AppConfig.class);
		assertAppConfigEquals("convert", appConfig, this.source);
	}

	@Test
	public void convertWithConversionService() {
		image.cdm.AppConfig appConfig = this.cs.convert(this.source, image.cdm.AppConfig.class);
		assertAppConfigEquals("convertWithConversionService", appConfig, this.source);
	}
}
