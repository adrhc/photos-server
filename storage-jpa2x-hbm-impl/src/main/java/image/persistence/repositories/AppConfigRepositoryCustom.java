package image.persistence.repositories;

import image.persistence.entity.enums.AppConfigEnum;

public interface AppConfigRepositoryCustom {
	void deleteAppConfig(AppConfigEnum ace);

	Integer getPhotosPerPage();

	String getAlbumsPath();

	void updateValue(String value, Integer appConfigId);

	String findByEnumeratedName(AppConfigEnum appConfigEnum);
}
