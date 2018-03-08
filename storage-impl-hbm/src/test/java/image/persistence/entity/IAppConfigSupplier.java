package image.persistence.entity;

import image.persistence.repository.util.IEnhancedRandom;
import image.persistence.util.IPositiveRandom;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by adr on 2/26/18.
 */
public interface IAppConfigSupplier extends IPositiveRandom, IEnhancedRandom {
	default AppConfig supplyEntityAppConfig() {
		return supplyEntityAppConfig(null, null);
	}

	default AppConfig supplyEntityAppConfig(String name, String value) {
		AppConfig appConfig = new AppConfig();
		int random = positiveRandom();
		appConfig.setName(name != null ? name : "entityAppConfig-" + random);
		appConfig.setValue(value != null ? value : "entityAppConfigValue-" + random);
		return appConfig;
	}

	default image.cdm.AppConfig randomCdmAppConfig(boolean withId) {
		return randomCdmAppConfigsStream(1, withId).findAny().get();
	}

	default List<image.cdm.AppConfig> randomCdmAppConfigsList(int amount, boolean withId) {
		return randomCdmAppConfigsStream(amount, withId).collect(Collectors.toList());
	}

	default Stream<image.cdm.AppConfig> randomCdmAppConfigsStream(int amount, boolean withId) {
		if (withId) {
			return IEnhancedRandom.random.objects(
					image.cdm.AppConfig.class, amount, "lastUpdate");
		} else {
			return IEnhancedRandom.random.objects(
					image.cdm.AppConfig.class, amount, "id", "lastUpdate");
		}
	}

	default image.cdm.AppConfig fixedCdmAppConfig(boolean withId) {
		image.cdm.AppConfig appConfig = new image.cdm.AppConfig();
		appConfig.setId(withId ? 1 : null);
		appConfig.setName("cdmAppConfig-1");
		appConfig.setValue("cdmAppConfigValue-1");
		return appConfig;
	}
}
