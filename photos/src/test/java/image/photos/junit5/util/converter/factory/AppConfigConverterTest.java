package image.photos.junit5.util.converter.factory;

import image.cdm.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.photos.junit5.config.IAppConfigAssertions;
import image.photos.junit5.config.testconfig.Junit5InMemoryDbPhotosConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

@NotThreadSafe
@Junit5InMemoryDbPhotosConfig
@Tag("misc")
public class AppConfigConverterTest implements IAppConfigSupplier, IAppConfigAssertions {
	@Autowired
	private ConversionService cs;
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