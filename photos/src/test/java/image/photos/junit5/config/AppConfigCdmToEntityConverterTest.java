package image.photos.junit5.config;

import image.cdm.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.photos.junit5.config.testconfig.Junit5InMemoryDbPhotosTestConfig;
import image.photos.util.PhotosConversionSupport;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@NotThreadSafe
@Tag("misc")
@Junit5InMemoryDbPhotosTestConfig
public class AppConfigCdmToEntityConverterTest implements IAppConfigSupplier {
	@Inject
	private PhotosConversionSupport photosConversionSupport;

	private AppConfig source;

	@BeforeEach
	void beforeAll() {
		this.source = supplyCdmAppConfig();
		this.source.setId(1);
	}

	@Test
	public void convert() {
		image.persistence.entity.AppConfig appConfig =
				this.photosConversionSupport.entityAppConfigOf(this.source);
		assertAllEquals("convert", this.source, appConfig);
	}
}
