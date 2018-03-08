package image.persistence.entity;

import image.persistence.repository.util.IEnhancedRandom;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by adr on 2/26/18.
 */
public interface IAppConfigSupplier extends IEnhancedRandom {
	default <T> T randomAppConfig(boolean withId, Class<T> clazz) {
		return randomAppConfigStream(1, withId, clazz).findAny().get();
	}

	default <T> List<T> randomAppConfigList(int amount, boolean withId, Class<T> clazz) {
		return randomAppConfigStream(amount, withId, clazz).collect(Collectors.toList());
	}

	default <T> Stream<T> randomAppConfigStream(int amount, boolean withId, Class<T> clazz) {
		if (withId) {
			return IEnhancedRandom.random.objects(
					clazz, amount, "lastUpdate");
		} else {
			return IEnhancedRandom.random.objects(
					clazz, amount, "id", "lastUpdate");
		}
	}

	default image.cdm.AppConfig fixedCdmAppConfig(boolean withId) {
		image.cdm.AppConfig appConfig = new image.cdm.AppConfig();
		appConfig.setId(withId ? 1 : null);
		appConfig.setName("cdmAppConfig-1");
		appConfig.setValue("cdmAppConfigValue-1");
		return appConfig;
	}

	default AppConfig entityAppConfigOf(String name, String value) {
		AppConfig appConfig = new AppConfig();
		appConfig.setName(name);
		appConfig.setValue(value);
		return appConfig;
	}
}
