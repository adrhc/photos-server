package image.photos.config;

import image.photos.TestPhotosConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assume.assumeTrue;

/**
 * Created by adrianpetre on 23.02.2018.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestPhotosConfig.class)
@TestPropertySource(properties = "hibernate.show_sql=false")
@ActiveProfiles({"jdbc-ds"})
@Category(TestPhotosConfig.class)
public class AppConfigServiceWriteTest {
    @Autowired
    private AppConfigService appConfigService;

    @Before
    public void setUp() {
        String path = appConfigService.getConfig("photos json FS path");
        assumeTrue(Files.isRegularFile(Paths.get(path)));
    }

    @Test
    public void writeJsonForAppConfigs() throws IOException {
        appConfigService.writeJsonForAppConfigs();
    }
}
