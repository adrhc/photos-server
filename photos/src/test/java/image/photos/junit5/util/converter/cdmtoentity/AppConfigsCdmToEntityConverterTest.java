package image.photos.junit5.util.converter.cdmtoentity;

import image.cdm.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.photos.junit5.appconfig.IAppConfigAssertions;
import image.photos.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import image.photos.util.conversion.PhotosConversionUtil;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NotThreadSafe
@Tag("misc")
@Junit5PhotosInMemoryDbConfig
public class AppConfigsCdmToEntityConverterTest implements IAppConfigSupplier, IAppConfigAssertions {
	@Inject
	private PhotosConversionUtil pcu;

	private List<AppConfig> appConfigs;

	@BeforeEach
	void setUp() {
		this.appConfigs = IntStream.range(0, 3).boxed()
				.map(i -> supplyCdmAppConfig())
				.collect(Collectors.toList());
	}

	@Test
	public void convert() {
		List<image.persistence.entity.AppConfig> cdmAppConfig =
				this.pcu.entityAppConfigsOf(this.appConfigs);
		assertAppConfigsEquals(this.appConfigs, cdmAppConfig);
	}
}
