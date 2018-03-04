package image.photos.junit5.config;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.photos.config.AppConfigsEntityToCdmConverter;
import image.photos.junit5.config.testconfig.Junit5InMemoryDbPhotosTestConfig;
import image.photos.util.ITypeDescriptors;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NotThreadSafe
@Tag("misc")
@Junit5InMemoryDbPhotosTestConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppConfigsEntityToCdmConverterTest implements IAppConfigSupplier {
	@Autowired
	private AppConfigsEntityToCdmConverter appConfigsEntityToCdmConverter;
	@Autowired
	private ConversionService cs;

	private List<AppConfig> appConfigs;

	@BeforeAll
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
				this.appConfigsEntityToCdmConverter.convert(this.appConfigs);
		assertListEquals(cdmAppConfig, this.appConfigs);
	}


	@Test
	public void convertWithConversionService() {
		List<image.cdm.AppConfig> cdmAppConfig = (List<image.cdm.AppConfig>)
				this.cs.convert(this.appConfigs,
						ITypeDescriptors.listOfEntityAppConfig,
						ITypeDescriptors.listOfCdmAppConfig);
		assertListEquals(cdmAppConfig, this.appConfigs);
	}

	private static void assertListEquals(List<image.cdm.AppConfig> cdmAppConfig,
	                                     List<AppConfig> appConfigs) {
		cdmAppConfig.forEach(cdm -> {
			long findings = appConfigs.stream().filter(ac ->
					ac.getId().equals(cdm.getId()) &&
							ac.getName().equals(cdm.getName()) &&
							ac.getValue().equals(cdm.getValue())).count();
			assertEquals(1, findings);
		});
	}
}
