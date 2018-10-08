package image.persistence.repository;

import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;

import java.util.Date;
import java.util.List;

/**
 * Created by adr on 2/22/18.
 */
public interface AppConfigRepository {
	void deleteAppConfig(AppConfigEnum ace);

	String findByEnumeratedName(AppConfigEnum appConfigEnum);

	Integer getPhotosPerPage();

	String getAlbumsPath();

	void updateValue(String value, Integer appConfigId);

	List<AppConfig> findAllOrderByNameAscNotCached();

	AppConfig findByNameNotCached(String name);

	Date getDBNow();

	AppConfig findByName(String name);

	List<AppConfig> findAll();

	AppConfig getById(Integer id);

	void persist(AppConfig appConfig);

	void deleteById(Integer id);

	void saveAll(Iterable<AppConfig> appConfigs);
}
