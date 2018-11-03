package image.persistence.entitytests;

import exifweb.util.random.IEnhancedRandom;
import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;

/**
 * Created by adr on 2/26/18.
 */
public interface IAppConfigSupplier extends IEnhancedRandom {
	default AppConfig entityAppConfigOf(AppConfigEnum name, String value) {
		return entityAppConfigOf(name.getValue(), value);
	}

	default AppConfig entityAppConfigOf(String name, String value) {
		AppConfig appConfig = new AppConfig();
		appConfig.setName(name);
		appConfig.setValue(value);
		return appConfig;
	}
}
