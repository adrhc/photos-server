package image.persistence.entity;

import image.persistence.util.IPositiveRandom;

/**
 * Created by adr on 2/26/18.
 */
public interface IAppConfigSupplier extends IPositiveRandom {
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

	default image.cdm.AppConfig supplyCdmAppConfig() {
		image.cdm.AppConfig appConfig = new image.cdm.AppConfig();
		int random = positiveRandom();
		appConfig.setName("cdmAppConfig-" + random);
		appConfig.setValue("cdmAppConfigValue-" + random);
		return appConfig;
	}
}
