package image.persistence.entity;

import image.persistence.util.IPositiveRandom;

import java.util.Date;

/**
 * Created by adr on 2/26/18.
 */
public interface IAppConfigSupplier extends IPositiveRandom {
	default AppConfig supplyAppConfig() {
		AppConfig appConfig = new AppConfig();
		int random = positiveRandom();
		appConfig.setName("appConfig-" + random);
		appConfig.setLastUpdate(new Date());
		appConfig.setValue("value-" + random);
		return appConfig;
	}
}
