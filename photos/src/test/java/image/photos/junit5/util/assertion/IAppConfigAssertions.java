package image.photos.junit5.util.assertion;

import image.cdm.AppConfig;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface IAppConfigAssertions {
	default void assertAppConfigEquals(String heading, AppConfig cdmAppConfig,
	                                   image.persistence.entity.AppConfig entityAppConfig) {
		assertAll(heading,
				() -> assertEquals(cdmAppConfig.getId(), entityAppConfig.getId()),
				() -> assertEquals(cdmAppConfig.getName(), entityAppConfig.getName()),
				() -> assertEquals(cdmAppConfig.getValue(), entityAppConfig.getValue()));
	}

	default void assertAppConfigsEquals(List<AppConfig> cdmAppConfigs,
	                                    List<image.persistence.entity.AppConfig> entityAppConfigs) {
		cdmAppConfigs.forEach(cdm -> {
			List<image.persistence.entity.AppConfig> foundEntityAppConfig =
					entityAppConfigs.stream().filter(ac -> ac.getId().equals(cdm.getId()))
							.collect(Collectors.toList());
			assertThat(foundEntityAppConfig, hasSize(1));
			assertAppConfigEquals("AppConfig: " + cdm.getName(), cdm, foundEntityAppConfig.get(0));
		});
	}
}
