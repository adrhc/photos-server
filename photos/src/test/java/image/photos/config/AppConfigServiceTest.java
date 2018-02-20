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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;

/**
 * Created by adr on 2/20/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestPhotosConfig.class)
@ActiveProfiles({"integration-tests", "jdbc-datasource"})
public class AppConfigServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigServiceTest.class);

	@Autowired
	private AppConfigService appConfigService;

	@Test
	public void getConfigs() {
		String photosJsonFSPath = appConfigService.getConfig("photos json FS path");
		assertThat(photosJsonFSPath, not(isEmptyOrNullString()));
		logger.debug("photosJsonFSPath = {}", photosJsonFSPath);

		// nothing to check here
		boolean bStopHttpdChecking = appConfigService.getConfigBool("stop_httpd_checking");
		logger.debug("bStopHttpdChecking = {}", bStopHttpdChecking);

		Boolean stopHttpdChecking = appConfigService.getConfigBoolean("stop_httpd_checking");
		assertNotNull(stopHttpdChecking);
		logger.debug("stopHttpdChecking = {}", stopHttpdChecking);

		Integer subtitlesExtractorLines = appConfigService.getConfigInteger("subtitles-extractor-lines");
		Assert.assertTrue(subtitlesExtractorLines > 0);
		logger.debug("subtitlesExtractorLines = {}", subtitlesExtractorLines);
	}

	@Test
	public void getLastUpdatedAppConfigs() {
		long lastUpdatedAppConfigs = appConfigService.getLastUpdatedAppConfigs();
		Assert.assertTrue(lastUpdatedAppConfigs > 0);
		logger.debug("lastUpdatedAppConfigs = {}", lastUpdatedAppConfigs);
	}

	@Test
	public void canUseJsonFilesLastUpdate() {
		long canUseJsonFilesLastUpdate = appConfigService.canUseJsonFilesLastUpdate();
		Assert.assertTrue(canUseJsonFilesLastUpdate > 0);
		logger.debug("canUseJsonFilesLastUpdate = {}", canUseJsonFilesLastUpdate);
	}
}
