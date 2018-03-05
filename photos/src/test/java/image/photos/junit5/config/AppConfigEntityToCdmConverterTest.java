package image.photos.junit5.config;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.photos.config.AppConfigEntityToCdmConverter;
import image.photos.junit5.config.testconfig.Junit5InMemoryDbPhotosTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

@NotThreadSafe
@Tag("misc")
@Junit5InMemoryDbPhotosTestConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppConfigEntityToCdmConverterTest implements IAppConfigSupplier, IAppConfigAssertions {
	@Autowired
	private AppConfigEntityToCdmConverter appConfigEntityToCdmConverter;
	@Autowired
	private ConversionService cs;

	private AppConfig source;

	@BeforeAll
	void beforeAll() {
		this.source = supplyEntityAppConfig();
		this.source.setId(1);
	}

	@Test
	public void convert() {
		image.cdm.AppConfig appConfig = this.appConfigEntityToCdmConverter.convert(this.source);
		assertAppConfigEquals("convert", appConfig, this.source);
	}

	@Test
	public void convertWithConversionService() {
		image.cdm.AppConfig appConfig = this.cs.convert(this.source, image.cdm.AppConfig.class);
		assertAppConfigEquals("convertWithConversionService", appConfig, this.source);
	}
}
