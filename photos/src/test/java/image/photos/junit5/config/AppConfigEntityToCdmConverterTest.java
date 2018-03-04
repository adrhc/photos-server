package image.photos.junit5.config;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.photos.config.AppConfigEntityToCdmConverter;
import image.photos.junit5.config.testconfig.Junit5InMemoryDbPhotosTestConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("misc")
@Junit5InMemoryDbPhotosTestConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppConfigEntityToCdmConverterTest implements IAppConfigSupplier {
	@Autowired
	private AppConfigEntityToCdmConverter appConfigEntityToCdmConverter;
	@Autowired
	private ConversionService cs;

	private AppConfig source;

	@BeforeAll
	void beforeAll() {
		this.source = supplyAppConfig();
		this.source.setId(1);
	}

	@Test
	public void convert() {
		image.cdm.AppConfig appConfig = this.appConfigEntityToCdmConverter.convert(this.source);
		assertAllEquals("convert", this.source, appConfig);
	}

	@Test
	public void convertWithConversionService() {
		image.cdm.AppConfig appConfig = this.cs.convert(this.source, image.cdm.AppConfig.class);
		assertAllEquals("convertWithConversionService", this.source, appConfig);
	}

	private static void assertAllEquals(String heading, AppConfig source,
	                                    image.cdm.AppConfig appConfig) {
		assertAll(heading,
				() -> assertEquals(source.getId(), appConfig.getId()),
				() -> assertEquals(source.getName(), appConfig.getName()),
				() -> assertEquals(source.getValue(), appConfig.getValue()));
	}
}
