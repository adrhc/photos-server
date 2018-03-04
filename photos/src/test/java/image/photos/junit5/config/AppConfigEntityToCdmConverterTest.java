package image.photos.junit5.config;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.photos.config.AppConfigEntityToCdmConverter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("junit5")
@Tag("photos")
@Tag("misc")
public class AppConfigEntityToCdmConverterTest implements IAppConfigSupplier {
	@Test
	public void convert() {
		AppConfig source = supplyAppConfig();
		source.setId(1);
		AppConfigEntityToCdmConverter converter = new AppConfigEntityToCdmConverter();
		image.cdm.AppConfig appConfig = converter.convert(source);
		assertAll("AppConfigEntityToCdmConverter",
				() -> assertEquals(source.getId(), appConfig.getId()),
				() -> assertEquals(source.getName(), appConfig.getName()),
				() -> assertEquals(source.getValue(), appConfig.getValue()));
	}
}
