package image.photos.config;

import image.photos.TestPhotosConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by adr on 2/20/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestPhotosConfig.class)
@ActiveProfiles("test-integration")
public class AppConfigServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigServiceTest.class);

	@Autowired
	private AppConfigService appConfigService;

	@Test
	public void getLastUpdatedAppConfigs() {
		long lastUpdatedAppConfigs = appConfigService.getLastUpdatedAppConfigs();
		logger.debug("lastUpdatedAppConfigs = {}", lastUpdatedAppConfigs);
		Assert.assertTrue(lastUpdatedAppConfigs > 0);
	}

	@Test
	public void canUseJsonFilesLastUpdate() {
		long canUseJsonFilesLastUpdate = appConfigService.canUseJsonFilesLastUpdate();
		logger.debug("canUseJsonFilesLastUpdate = {}", canUseJsonFilesLastUpdate);
		Assert.assertTrue(canUseJsonFilesLastUpdate > 0);
	}
}
