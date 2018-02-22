package image.persistence.integration.repository;

import image.persistence.entity.AppConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by adr on 2/22/18.
 */
public interface AppConfigRepository {
	Integer getPhotosPerPage();

	String getLinuxAlbumPath();

	@Transactional
	AppConfig getAppConfigById(Integer id);

	@Transactional
	AppConfig getAppConfigByName(String name);

	@Transactional
	void update(List<AppConfig> appConfigs);

	@Transactional(readOnly = true)
	List<AppConfig> getAppConfigs();

	@Transactional(readOnly = true)
	List<AppConfig> testGetNoCacheableOrderedAppConfigs();

	@Transactional(readOnly = true)
	AppConfig testGetNoCacheableAppConfigByName(String name);

	@Transactional(readOnly = true)
	Date getDBNow();
}
