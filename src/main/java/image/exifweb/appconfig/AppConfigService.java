package image.exifweb.appconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.exifweb.system.persistence.entities.AppConfig;
import image.exifweb.system.persistence.entities.enums.AppConfigEnum;
import image.exifweb.system.persistence.repositories.AppConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AppConfigService {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigService.class);
	@Inject
	private ObjectMapper json;
	@Inject
	private AppConfigRepository appConfigRepository;

	public Integer getConfigInteger(AppConfigEnum ace) {
		String s = getConfig(ace);
		if (s == null) {
			return null;
		}
		return new Integer(s);
	}

	public int getPhotosPerPage() {
		return getConfigInt(AppConfigEnum.PHOTOS_PER_PAGE);
	}

	public String getLinuxAlbumPath() {
		return getConfig(AppConfigEnum.LINUX_ALBUMS_PATH);
	}

	public Integer getConfigInt(AppConfigEnum ace) {
		String s = getConfig(ace);
		if (s == null) {
			return 0;
		}
		return Integer.parseInt(s);
	}

	public String getConfig(AppConfigEnum appConfigEnum) {
		return appConfigRepository.getAppConfigById(appConfigEnum.getValue()).getValue();
	}

	public boolean getConfigBool(String name) {
		String s = getConfig(name);
		return Boolean.parseBoolean(s);
	}

	public Boolean getConfigBoolean(String name) {
		String s = getConfig(name);
		return Boolean.valueOf(s);
	}

	public Integer getConfigInt(String name) {
		String s = getConfig(name);
		if (s == null) {
			return 0;
		}
		return Integer.parseInt(s);
	}

	public String getConfig(String name) {
		AppConfig ac = appConfigRepository.getAppConfigByName(name);
		if (ac == null) {
			return null;
		}
		return ac.getValue();
	}

	/**
	 * Metoda cu dublu scope:
	 * - update appConfigs.json
	 * - update lastUpdatedAppConfigs
	 *
	 * @throws IOException
	 */
	public void writeJsonForAppConfigs() throws IOException {
		File dir = new File(getConfig("photos json FS path"));
		dir.mkdirs();
		File file = new File(dir, "appConfigs.json");
		List<AppConfig> appConfigs = appConfigRepository.getAppConfigs();
//        logger.debug(ArrayUtils.toString(appConfigs));
//        logger.debug("lastUpdatedAppConfigs = {}", getLastUpdatedAppConfigs());
		json.writeValue(file, appConfigs);
	}

	public long getLastUpdatedAppConfigs() {
//        logger.debug("BEGIN");
		List<AppConfig> appConfigs = appConfigRepository.getAppConfigs();
		Date date = null;
		for (AppConfig appConfig : appConfigs) {
			if (date == null) {
				date = appConfig.getLastUpdate();
			} else if (date.before(appConfig.getLastUpdate())) {
				date = appConfig.getLastUpdate();
			}
//            logger.debug("{} = {}, date = " + date.getTime(),
//                appConfig.getName(), appConfig.getLastUpdate().getTime());
		}
//        logger.debug("END {}", date.getTime());
		if (date == null) {
			return -1;
		}
		return date.getTime();
	}

	public long canUseJsonFilesLastUpdate() {
		AppConfig useJsonFiles = appConfigRepository.getAppConfigByName("use json files");
		AppConfig useJsonFilesForConfig = appConfigRepository.getAppConfigByName("use json files for config");
		if (useJsonFiles.getLastUpdate().after(useJsonFilesForConfig.getLastUpdate())) {
//            logger.debug("END {}", useJsonFiles.getLastUpdate().getTime());
			return useJsonFiles.getLastUpdate().getTime();
		} else {
//            logger.debug("END {}", useJsonFilesForConfig.getLastUpdate().getTime());
			return useJsonFilesForConfig.getLastUpdate().getTime();
		}
	}
}
