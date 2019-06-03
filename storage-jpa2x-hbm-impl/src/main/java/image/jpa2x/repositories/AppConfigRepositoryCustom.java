package image.jpa2x.repositories;

import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;

import java.util.List;

public interface AppConfigRepositoryCustom {
	void deleteByEnumeratedName(AppConfigEnum ace);

	Integer getPhotosPerPage();

	String getAlbumsPath();

	void updateValue(String value, Integer appConfigId);

	AppConfig findByEnumeratedName(AppConfigEnum appConfigEnum);

	String findValueByEnumeratedName(AppConfigEnum appConfigEnum);

	void updateAll(List<AppConfig> appConfigs);
}
