package image.persistence.entity;

import image.persistence.util.IPositiveRandom;

/**
 * Created by adr on 2/26/18.
 */
public interface IAppConfigSupplier extends IPositiveRandom {
	default AppConfig supplyAppConfig() {
		return supplyAppConfig(null, null);
	}

	default AppConfig supplyAppConfig(String name, String value) {
		AppConfig appConfig = new AppConfig();
		int random = positiveRandom();
		appConfig.setName(name != null ? name : "appConfig-" + random);
		appConfig.setValue(value != null ? value : "value-" + random);
		return appConfig;
	}
}
