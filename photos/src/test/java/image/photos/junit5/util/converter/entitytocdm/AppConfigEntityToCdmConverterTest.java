package image.photos.junit5.util.converter.entitytocdm;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.photos.junit5.config.IAppConfigAssertions;
import image.photos.junit5.util.converter.ConversionTestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppConfigEntityToCdmConverterTest
		extends ConversionTestBase
		implements IAppConfigSupplier, IAppConfigAssertions {
	private AppConfig source;

	@BeforeAll
	void beforeAll() {
		this.source = supplyEntityAppConfig();
		this.source.setId(1);
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
