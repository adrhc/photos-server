package image.photos.junit5.util.converter.entitytocdm;

import image.persistence.entity.AppConfig;
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
@Junit5PhotosInMemoryDbConfig
@Tag("misc")
public class AppConfigsEntityToCdmConverterTest implements IAppConfigSupplier, IAppConfigAssertions {
	@Inject
	private PhotosConversionUtil photosConversionSupport;

	private List<AppConfig> appConfigs;

	@BeforeEach
	void beforeAll() {
		this.appConfigs = IntStream.range(0, 3).boxed()
				.map(i -> {
					AppConfig source = supplyEntityAppConfig();
					source.setId(i);
					return source;
				})
				.collect(Collectors.toList());
	}

	@Test
	public void convert() {
		List<image.cdm.AppConfig> cdmAppConfig =
				this.photosConversionSupport.cdmAppConfigsOf(this.appConfigs);
		assertAppConfigsEquals(cdmAppConfig, this.appConfigs);
	}
}
