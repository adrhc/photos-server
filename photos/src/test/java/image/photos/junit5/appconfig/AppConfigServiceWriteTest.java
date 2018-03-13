package image.photos.junit5.appconfig;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.repository.AppConfigRepository;
import image.photos.config.AppConfigService;
import image.photos.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import io.github.glytching.junit.extension.folder.TemporaryFolder;
import io.github.glytching.junit.extension.folder.TemporaryFolderExtension;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by adrianpetre on 23.02.2018.
 */
@NotThreadSafe
@Junit5PhotosInMemoryDbConfig
public class AppConfigServiceWriteTest implements IAppConfigSupplier {
	@Autowired
	private AppConfigRepository appConfigRepository;
	@Autowired
	private AppConfigService appConfigService;
	@Autowired
	private ConversionService conversionService;

	@BeforeEach
	void setUp() {
		randomInstanceStream(5, false, AppConfig.class)
				.forEach(this.appConfigRepository::createAppConfig);
	}

	@Test
	@ExtendWith(TemporaryFolderExtension.class)
	public void writeJsonForAppConfigs(TemporaryFolder temporaryFolder) throws IOException {
		File dir = temporaryFolder.createDirectory("writeJsonForAppConfigs");
		createPhotosJsonFSPathAppConfig(dir.getAbsolutePath());
		File file = this.appConfigService.writeJsonForAppConfigs();
		assertTrue(Files.isRegularFile(file.toPath()));
	}

	private void createPhotosJsonFSPathAppConfig(String photosJsonFSPath) {
		AppConfig appConfig = new AppConfig();
		appConfig.setName(AppConfigEnum.photos_json_FS_path.getValue());
		appConfig.setValue(photosJsonFSPath);
		this.appConfigRepository.createAppConfig(appConfig);
	}
}
