package image.photos.junit4.appconfig;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.repository.AppConfigRepository;
import image.photos.config.AppConfigService;
import image.photos.springconfig.PhotosInMemoryDbConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by adrianpetre on 23.02.2018.
 */
@RunWith(SpringRunner.class)
@NotThreadSafe
@PhotosInMemoryDbConfig
@Category(PhotosInMemoryDbConfig.class)
public class AppConfigServiceWriteTest implements IAppConfigSupplier {
	@Autowired
	private AppConfigRepository appConfigRepository;
	@Autowired
	private AppConfigService appConfigService;

	private String TEMP_DIR_PREFIX = "writeJsonForAppConfigs-";
	private Path tempDir;

	@Before
	public void setUp() throws IOException {
		// temp directory
		EnumSet<PosixFilePermission> perms = EnumSet.of(PosixFilePermission.OWNER_READ,
				PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE,
				PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_EXECUTE);
		this.tempDir = Files.createTempDirectory(this.TEMP_DIR_PREFIX,
				PosixFilePermissions.asFileAttribute(perms));
		// photosJsonFSPath
		AppConfig photosJsonFSPath = new AppConfig();
		photosJsonFSPath.setName(AppConfigEnum.photos_json_FS_path.getValue());
		photosJsonFSPath.setValue(this.tempDir.toAbsolutePath().toString());
		this.appConfigRepository.createAppConfig(photosJsonFSPath);
		// some other random AppConfig
		randomInstanceStream(3, false, AppConfig.class)
				.forEach(this.appConfigRepository::createAppConfig);
	}

	@Test
	public void writeJsonForAppConfigs() throws IOException {
		this.appConfigService.writeJsonForAppConfigs();
		assertTrue(Files.isRegularFile(this.tempDir.resolve("appConfigs.json")));
	}

	@After
	public void teadDown() throws IOException {
		assertTrue(this.tempDir.getFileName().toString().startsWith(this.TEMP_DIR_PREFIX));
		Files.walkFileTree(this.tempDir,
				new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult postVisitDirectory(
							Path dir, IOException exc) throws IOException {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(
							Path file, BasicFileAttributes attrs)
							throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}
				});
		assertFalse(this.tempDir + " still exists", Files.exists(this.tempDir));
	}
}
