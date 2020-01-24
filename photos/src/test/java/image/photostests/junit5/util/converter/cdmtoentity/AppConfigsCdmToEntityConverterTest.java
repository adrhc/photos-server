package image.photostests.junit5.util.converter.cdmtoentity;

import image.cdm.AppConfig;
import image.persistence.entitytests.IAppConfigSupplier;
import image.photos.config.AppConfigConversionHelper;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import image.photostests.junit5.util.assertion.IAppConfigAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Junit5PhotosInMemoryDbConfig
@Tag("misc")
public class AppConfigsCdmToEntityConverterTest implements IAppConfigSupplier, IAppConfigAssertions {
	@Autowired
	private AppConfigConversionHelper pcu;

	private List<AppConfig> appConfigs;

	@BeforeAll
	void beforeAll() {
		this.appConfigs = this.randomInstanceList(3, true, AppConfig.class);
	}

	@Test
	public void convert() {
		List<image.persistence.entity.AppConfig> cdmAppConfig =
				this.pcu.entityAppConfigsOf(this.appConfigs);
		this.assertAppConfigsEquals(this.appConfigs, cdmAppConfig);
	}
}
