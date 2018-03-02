package image.photos.config;

import image.persistence.entity.AppConfig;
import image.persistence.repository.AppConfigRepository;
import image.photos.springtestconfig.InMemoryDbPhotosTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;

/**
 * Created by adrianpetre on 23.02.2018.
 */
@RunWith(SpringRunner.class)
@NotThreadSafe
@InMemoryDbPhotosTestConfig
@Category(InMemoryDbPhotosTestConfig.class)
public class AppConfigServiceWriteTest {
	@Autowired
	private AppConfigRepository appConfigRepository;
	@Autowired
	private AppConfigService appConfigService;

	@Before
	public void setUp() throws IOException {
		EnumSet<PosixFilePermission> perms = EnumSet.of(PosixFilePermission.OWNER_READ,
				PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_EXECUTE);
		Path tempDir = Files.createTempDirectory("writeJsonForAppConfigs-",
				PosixFilePermissions.asFileAttribute(perms));
		AppConfig photosJsonFSPath = new AppConfig();
		photosJsonFSPath.setName("photos json FS path");
		photosJsonFSPath.setValue(tempDir.toAbsolutePath().toString());
		this.appConfigRepository.createAppConfig(photosJsonFSPath);
		AppConfig dummy = new AppConfig();
		dummy.setName("dummy-name");
		dummy.setValue("dummy-value");
		this.appConfigRepository.createAppConfig(dummy);
	}

	@Test
	public void writeJsonForAppConfigs() throws IOException {
		this.appConfigService.writeJsonForAppConfigs();
	}
}
