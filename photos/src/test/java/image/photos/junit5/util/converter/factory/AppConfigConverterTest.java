package image.photos.junit5.util.converter.factory;

import image.cdm.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.photos.junit5.config.IAppConfigAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AppConfigConverterTest extends ConverterFactoryTestBase
		implements IAppConfigSupplier, IAppConfigAssertions {
	private AppConfig sourceAppConfig;

	@BeforeEach
	void setUp() {
		this.sourceAppConfig = supplyCdmAppConfig();
	}

	@Test
	void convert() {
		image.persistence.entity.AppConfig appConfig = this.
				cs.convert(this.sourceAppConfig, image.persistence.entity.AppConfig.class);
		assertAppConfigEquals("appConfig", this.sourceAppConfig, appConfig);
	}
}