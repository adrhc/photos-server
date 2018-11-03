package image.photos.junit5.util.converter.cdmtoentity;

import image.cdm.AppConfig;
import image.persistence.entitytests.IAppConfigSupplier;
import image.photos.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import image.photos.junit5.util.assertion.IAppConfigAssertions;
import image.photos.util.conversion.PhotosConversionUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

@Junit5PhotosInMemoryDbConfig
@Tag("misc")
public class AppConfigsCdmToEntityConverterTest implements IAppConfigSupplier, IAppConfigAssertions {
	@Inject
	private PhotosConversionUtil pcu;

	private List<AppConfig> appConfigs;

	@BeforeAll
	void beforeAll() {
		this.appConfigs = randomInstanceList(3, true, AppConfig.class);
	}

	@Test
	public void convert() {
		List<image.persistence.entity.AppConfig> cdmAppConfig =
				this.pcu.entityAppConfigsOf(this.appConfigs);
		assertAppConfigsEquals(this.appConfigs, cdmAppConfig);
	}
}
