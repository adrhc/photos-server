package image.jpa2x.repositories;

import image.persistence.entity.enums.AppConfigEnum;

public interface AppConfigRepositoryCustom {
	void deleteByEnumeratedName(AppConfigEnum ace);

	Integer getPhotosPerPage();

	String getAlbumsPath();

	void updateValue(String value, Integer appConfigId);

	String findValueByEnumeratedName(AppConfigEnum appConfigEnum);
}
