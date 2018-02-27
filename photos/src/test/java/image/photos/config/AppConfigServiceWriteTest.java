package image.photos.config;

import image.photos.TestPhotosConfig;
import image.photos.springtestconfig.JdbcDsPhotosTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assume.assumeTrue;

/**
 * Created by adrianpetre on 23.02.2018.
 */
@RunWith(SpringRunner.class)
@NotThreadSafe
@JdbcDsPhotosTestConfig
@Category(TestPhotosConfig.class)
public class AppConfigServiceWriteTest {
	@Autowired
	private AppConfigService appConfigService;

	@Before
	public void setUp() {
		String path = appConfigService.getConfig("photos json FS path");
		assumeTrue("missing " + path, Files.isDirectory(Paths.get(path)));
	}

	@Ignore("todo: use ramfs")
	@Test
	public void writeJsonForAppConfigs() throws IOException {
		appConfigService.writeJsonForAppConfigs();
	}
}
