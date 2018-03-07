package image.photos.junit5.appconfig;

import image.cdm.AppConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface IAppConfigAssertions {

	default void assertAppConfigEquals(String heading, image.cdm.AppConfig cdmAppConfig,
	                                   image.persistence.entity.AppConfig entityAppConfig) {
		assertAll(heading,
				() -> assertEquals(cdmAppConfig.getId(), entityAppConfig.getId()),
				() -> assertEquals(cdmAppConfig.getName(), entityAppConfig.getName()),
				() -> assertEquals(cdmAppConfig.getValue(), entityAppConfig.getValue()));
	}

	default void assertAppConfigsEquals(List<AppConfig> cdmAppConfigs,
	                                    List<image.persistence.entity.AppConfig> entityAppConfigs) {
		cdmAppConfigs.forEach(cdm -> {
			long findings = entityAppConfigs.stream().filter(ac ->
					ac.getId().equals(cdm.getId()) &&
							ac.getName().equals(cdm.getName()) &&
							ac.getValue().equals(cdm.getValue())).count();
			assertEquals(1, findings);
		});
	}
}
