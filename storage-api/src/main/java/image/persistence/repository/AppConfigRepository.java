package image.persistence.repository;

import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;

import java.util.Date;
import java.util.List;

/**
 * Created by adr on 2/22/18.
 */
public interface AppConfigRepository {
	void createAppConfig(AppConfig appConfig);

	void deleteAppConfig(AppConfigEnum ace);

	void deleteById(Integer id);

	String getConfig(AppConfigEnum appConfigEnum);

	Integer getPhotosPerPage();

	String getAlbumsPath();

	AppConfig getAppConfigById(Integer id);

	AppConfig getAppConfigByName(String name);

	void update(List<AppConfig> appConfigs);

	void updateValue(String value, Integer appConfigId);

	List<AppConfig> getAppConfigs();

	List<AppConfig> testGetNoCacheableOrderedAppConfigs();

	AppConfig testGetNoCacheableAppConfigByName(String name);

	Date getDBNow();
}
