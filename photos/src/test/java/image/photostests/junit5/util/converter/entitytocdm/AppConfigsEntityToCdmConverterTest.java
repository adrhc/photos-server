package image.photostests.junit5.util.converter.entitytocdm;

import image.persistence.entity.AppConfig;
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
public class AppConfigsEntityToCdmConverterTest implements IAppConfigSupplier, IAppConfigAssertions {
	@Autowired
	private AppConfigConversionHelper photosConversionSupport;

	private List<AppConfig> appConfigs;

	@BeforeAll
	void beforeAll() {
		this.appConfigs = this.randomInstanceList(3, true, AppConfig.class);
	}

	@Test
	public void convert() {
		List<image.cdm.AppConfig> cdmAppConfig =
				this.photosConversionSupport.cdmAppConfigsOf(this.appConfigs);
		this.assertAppConfigsEquals(cdmAppConfig, this.appConfigs);
	}
}
