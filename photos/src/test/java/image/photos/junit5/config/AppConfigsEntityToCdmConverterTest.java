package image.photos.junit5.config;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.photos.config.AppConfigEntityToCdmConverter;
import image.photos.config.AppConfigsEntityToCdmConverter;
import image.photos.junit5.config.testconfig.Junit5InMemoryDbPhotosTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Junit5InMemoryDbPhotosTestConfig
public class AppConfigsEntityToCdmConverterTest implements IAppConfigSupplier {
	@Autowired
	private AppConfigEntityToCdmConverter appConfigEntityToCdmConverter;
	@Autowired
	private AppConfigsEntityToCdmConverter appConfigsEntityToCdmConverter;

	private List<AppConfig> appConfigs;

	@BeforeEach
	void setUp() {
		this.appConfigs = IntStream.range(0, 3).boxed()
				.map(i -> {
					AppConfig source = supplyAppConfig();
					source.setId(i);
					return source;
				})
				.collect(Collectors.toList());
	}

	@Test
	public void convert() {
		List<image.cdm.AppConfig> cdmAppConfig =
				this.appConfigsEntityToCdmConverter.convert(this.appConfigs);
		cdmAppConfig.forEach(cdm -> {
			long findings = this.appConfigs.stream().filter(ac ->
					ac.getId().equals(cdm.getId()) &&
							ac.getName().equals(cdm.getName()) &&
							ac.getValue().equals(cdm.getValue())).count();
			assertEquals(1, findings);
		});
	}
}
