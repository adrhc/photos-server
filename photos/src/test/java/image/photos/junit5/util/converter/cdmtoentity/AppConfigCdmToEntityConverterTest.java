package image.photos.junit5.util.converter.cdmtoentity;

import image.cdm.AppConfig;
import image.persistence.entitytests.IAppConfigSupplier;
import image.photos.junit5.util.assertion.IAppConfigAssertions;
import image.photos.junit5.util.converter.ConversionTestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AppConfigCdmToEntityConverterTest extends ConversionTestBase
		implements IAppConfigSupplier, IAppConfigAssertions {
	private AppConfig sourceAppConfig;

	@BeforeAll
	void beforeAll() {
		this.sourceAppConfig = randomInstance(true, AppConfig.class);
	}

	@Test
	void convert() {
		image.persistence.entity.AppConfig appConfig = this.
				cs.convert(this.sourceAppConfig, image.persistence.entity.AppConfig.class);
		assertAppConfigEquals("appConfig", this.sourceAppConfig, appConfig);
	}
}
