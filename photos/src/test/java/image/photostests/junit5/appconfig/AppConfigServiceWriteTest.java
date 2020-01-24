package image.photostests.junit5.appconfig;

import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2x.repositories.appconfig.AppConfigRepository;
import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.entitytests.IAppConfigSupplier;
import image.photos.config.AppConfigService;
import image.photos.util.conversion.PhotosConversionUtil;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import image.photostests.junit5.util.assertion.IAppConfigAssertions;
import io.github.glytching.junit.extension.folder.TemporaryFolder;
import io.github.glytching.junit.extension.folder.TemporaryFolderExtension;
import io.github.glytching.junit.extension.random.Random;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by adrianpetre on 23.02.2018.
 */
@Junit5PhotosInMemoryDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
public class AppConfigServiceWriteTest implements IAppConfigAssertions, IAppConfigSupplier {
	@PersistenceContext
	protected EntityManager em;
	@Autowired
	private AppConfigRepository appConfigRepository;
	@Autowired
	private AppConfigService appConfigService;
	@Autowired
	private PhotosConversionUtil photosConversionUtil;

	@Random(type = AppConfig.class, size = 20, excludes = {"id", "lastUpdate"})
	private List<AppConfig> appConfigs;

	@BeforeAll
	void beforeAll() {
		this.appConfigs.forEach(this.appConfigRepository::persist);
	}

	@Test
	@ExtendWith(TemporaryFolderExtension.class)
	public void writeJsonForAppConfigs(TemporaryFolder temporaryFolder) throws IOException {
		File dir = temporaryFolder.createDirectory("writeJsonForAppConfigs");
		this.insertPhotosJsonFSPathAppConfig(dir.getAbsolutePath());
		Path file = this.appConfigService.writeJsonForAppConfigs();
		assertTrue(Files.isRegularFile(file));
		List<image.cdm.AppConfig> appConfigsOfJson = this.loadCdmAppConfigsFromFile(file);
		this.assertAppConfigsEquals(appConfigsOfJson, this.appConfigs);
	}

	@Test
	void cacheTest() {
		Integer id = this.appConfigs.get(0).getId();
		this.appConfigRepository.getById(id);
		Cache cache = this.em.getEntityManagerFactory().getCache();
		// cache.entityAccessMap.entrySet().toArray()[3].getValue().storageAccess.cache.compoundStore.map
		assertTrue(cache.contains(AppConfig.class, id), "AppConfig:" + id + " not in cache!");
		this.appConfigService.evictAppConfigCache();
		assertFalse(cache.contains(AppConfig.class, id), "AppConfig:" + id + " already in cache!");
	}

	private List<image.cdm.AppConfig> loadCdmAppConfigsFromFile(Path path) throws IOException {
		String json = FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8);
		return this.photosConversionUtil.cdmAppConfigsOf(json);
	}

	private void insertPhotosJsonFSPathAppConfig(String photosJsonFSPath) {
		AppConfig appConfig = new AppConfig();
		appConfig.setName(AppConfigEnum.photos_json_FS_path.getValue());
		appConfig.setValue(photosJsonFSPath);
		this.appConfigRepository.persist(appConfig);
		this.appConfigs.add(appConfig);
	}
}
