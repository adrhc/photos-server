package image.photos.junit5.config;

import image.persistence.entity.AppConfig;
import image.persistence.repository.AppConfigRepository;
import image.photos.config.AppConfigService;
import image.photos.springtestconfig.InMemoryDbPhotosTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

/**
 * Created by adrianpetre on 23.02.2018.
 */
@NotThreadSafe
@ExtendWith(SpringExtension.class)
@InMemoryDbPhotosTestConfig
@Tag("junit5")
@Tag("photos")
@Tag("inmemorydb")
public class AppConfigServiceWriteTest {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigService.class);
	@Autowired
	private AppConfigRepository appConfigRepository;
	@Autowired
	private AppConfigService appConfigService;
	private Path tempDir;

	@BeforeEach
	public void setUp() throws IOException {
		// temp directory
		EnumSet<PosixFilePermission> perms = EnumSet.of(PosixFilePermission.OWNER_READ,
				PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE,
				PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_EXECUTE);
		this.tempDir = Files.createTempDirectory("writeJsonForAppConfigs-",
				PosixFilePermissions.asFileAttribute(perms));
		// photosJsonFSPath
		AppConfig photosJsonFSPath = new AppConfig();
		photosJsonFSPath.setName("photos json FS path");
		photosJsonFSPath.setValue(this.tempDir.toAbsolutePath().toString());
		logger.debug("{}:\n{}", photosJsonFSPath.getName(), photosJsonFSPath.getValue());
		this.appConfigRepository.createAppConfig(photosJsonFSPath);
		// dummy AppConfig
		AppConfig dummy = new AppConfig();
		dummy.setName("dummy-name");
		dummy.setValue("dummy-value");
		this.appConfigRepository.createAppConfig(dummy);
	}

	@Test
	public void writeJsonForAppConfigs() throws IOException {
		this.appConfigService.writeJsonForAppConfigs();
	}

	@AfterEach
	public void teadDown() throws IOException {
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
