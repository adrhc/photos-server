package image.photos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.jpa2x.repositories.appconfig.AppConfigRepository;
import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;
import image.photos.infrastructure.filestore.FileStoreService;
import image.photos.util.conversion.PhotosConversionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.nio.file.Path;
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
	private final ObjectMapper objectMapper;
	private final AppConfigRepository appConfigRepository;
	private final PhotosConversionUtil photosConversionSupport;
	private final FileStoreService fileStoreService;
	@PersistenceContext
	private EntityManager em;
	@Value("${app.configs.file}")
	private String appConfigsFile;

	public AppConfigService(FileStoreService fileStoreService, PhotosConversionUtil photosConversionSupport, ObjectMapper objectMapper, AppConfigRepository appConfigRepository) {
		this.fileStoreService = fileStoreService;
		this.photosConversionSupport = photosConversionSupport;
		this.objectMapper = objectMapper;
		this.appConfigRepository = appConfigRepository;
	}

	public boolean getConfigBool(AppConfigEnum name) {
		return this.getConfigBool(name.getValue());
	}

	public boolean getConfigBool(String name) {
		String s = this.getConfig(name);
		return Boolean.parseBoolean(s);
	}

	public Boolean getConfigBoolean(AppConfigEnum name) {
		return this.getConfigBoolean(name.getValue());
	}

	public Boolean getConfigBoolean(String name) {
		String s = this.getConfig(name);
		return Boolean.valueOf(s);
	}

	public Integer getConfigInteger(AppConfigEnum name) {
		return this.getConfigInteger(name.getValue());
	}

	public Integer getConfigInteger(String name) {
		String s = this.getConfig(name);
		if (s == null) {
			return 0;
		}
		return Integer.valueOf(s);
	}

	public String getConfig(AppConfigEnum name) {
		return this.getConfig(name.getValue());
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
	public Path writeJsonForAppConfigs() throws IOException {
		Path dir = Path.of(this.appConfigRepository
				.findValueByEnumeratedName(AppConfigEnum.photos_json_FS_path));
		this.fileStoreService.createDirectories(dir);
		Path file = dir.resolve(this.appConfigsFile);
		List<AppConfig> appConfigs = this.appConfigRepository.findAll();
//        logger.debug(ArrayUtils.toString(appConfigs));
//        logger.debug("lastUpdatedAppConfigs = {}", getLastUpdatedAppConfigs());
		this.objectMapper.writeValue(file.toFile(),
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

	/**
	 * updates appConfigs into DB
	 */
	public void updateAll(List<AppConfig> appConfigs) {
		this.appConfigRepository.updateAll(appConfigs);
	}

	public void evictAppConfigCache() {
		this.em.getEntityManagerFactory().getCache().evict(AppConfig.class);
	}
}
