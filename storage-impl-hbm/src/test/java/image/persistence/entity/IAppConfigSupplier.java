package image.persistence.entity;

import image.persistence.repository.util.random.IEnhancedRandom;

/**
 * Created by adr on 2/26/18.
 */
public interface IAppConfigSupplier extends IEnhancedRandom {
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
