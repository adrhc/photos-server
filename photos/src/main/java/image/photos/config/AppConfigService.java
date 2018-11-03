package image.photos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.jpa2x.repositories.AppConfigRepository;
import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;
import image.photos.util.conversion.PhotosConversionUtil;
import org.springframework.beans.factory.annotation.Value;
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
	//	private static final Logger logger = LoggerFactory.getLogger(AppConfigService.class);
	@Inject
	private ObjectMapper objectMapper;
	@Inject
	private AppConfigRepository appConfigRepository;
	@Inject
	private PhotosConversionUtil photosConversionSupport;
	@Value("${app.configs.file}")
	private String appConfigsFile;

	public boolean getConfigBool(String name) {
		String s = getConfig(name);
		return Boolean.parseBoolean(s);
	}

	public Boolean getConfigBoolean(String name) {
		String s = getConfig(name);
		return Boolean.valueOf(s);
	}

	public Integer getConfigInteger(String name) {
		String s = getConfig(name);
		if (s == null) {
			return 0;
		}
		return new Integer(s);
	}

	public String getConfig(String name) {
		AppConfig ac = this.appConfigRepository.findByName(name);
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
	public File writeJsonForAppConfigs() throws IOException {
		File dir = new File(this.appConfigRepository.findValueByEnumeratedName(AppConfigEnum.photos_json_FS_path));
		dir.mkdirs();
		File file = new File(dir, this.appConfigsFile);
		List<AppConfig> appConfigs = this.appConfigRepository.findAll();
//        logger.debug(ArrayUtils.toString(appConfigs));
//        logger.debug("lastUpdatedAppConfigs = {}", getLastUpdatedAppConfigs());
		this.objectMapper.writeValue(file,
				this.photosConversionSupport.cdmAppConfigsOf(appConfigs));
		return file;
	}

	public long getLastUpdatedAppConfigs() {
//        logger.debug("BEGIN");
		List<AppConfig> appConfigs = this.appConfigRepository.findAll();
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
		AppConfig useJsonFiles = this.appConfigRepository.findByName("use json files");
		AppConfig useJsonFilesForConfig = this.appConfigRepository.findByName("use json files for config");
		if (useJsonFiles.getLastUpdate().after(useJsonFilesForConfig.getLastUpdate())) {
//            logger.debug("END {}", useJsonFiles.getLastUpdate().getTime());
			return useJsonFiles.getLastUpdate().getTime();
		} else {
//            logger.debug("END {}", useJsonFilesForConfig.getLastUpdate().getTime());
			return useJsonFilesForConfig.getLastUpdate().getTime();
		}
	}
}
