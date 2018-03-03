package image.photos.junit4.config;

import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.repository.AppConfigRepository;
import image.photos.config.AppConfigService;
import image.photos.springtestconfig.ProdJdbcDsPhotosTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;

/**
 * Created by adr on 2/20/18.
 */
@RunWith(SpringRunner.class)
@NotThreadSafe
@ProdJdbcDsPhotosTestConfig
@Category(ProdJdbcDsPhotosTestConfig.class)
public class AppConfigServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigServiceTest.class);

	@Autowired
	private AppConfigRepository appConfigRepository;
	@Autowired
	private AppConfigService appConfigService;

	@Test
	public void getConfigs() {
		String photosJsonFSPath = this.appConfigRepository.getConfig(AppConfigEnum.photos_json_FS_path);
		assertThat(photosJsonFSPath, not(isEmptyOrNullString()));
		logger.debug("photosJsonFSPath = {}", photosJsonFSPath);

		// nothing to check here
		boolean bStopHttpdChecking = this.appConfigService.getConfigBool("stop_httpd_checking");
		logger.debug("bStopHttpdChecking = {}", bStopHttpdChecking);

		Boolean stopHttpdChecking = this.appConfigService.getConfigBoolean("stop_httpd_checking");
		assertNotNull(stopHttpdChecking);
		logger.debug("stopHttpdChecking = {}", stopHttpdChecking);

		Integer subtitlesExtractorLines = this.appConfigService.getConfigInteger("subtitles-extractor-lines");
		Assert.assertTrue(subtitlesExtractorLines > 0);
		logger.debug("subtitlesExtractorLines = {}", subtitlesExtractorLines);
	}

	@Test
	public void getLastUpdatedAppConfigs() {
		long lastUpdatedAppConfigs = this.appConfigService.getLastUpdatedAppConfigs();
		Assert.assertTrue(lastUpdatedAppConfigs > 0);
		logger.debug("lastUpdatedAppConfigs = {}", lastUpdatedAppConfigs);
	}

	@Test
	public void canUseJsonFilesLastUpdate() {
		long canUseJsonFilesLastUpdate = this.appConfigService.canUseJsonFilesLastUpdate();
		Assert.assertTrue(canUseJsonFilesLastUpdate > 0);
		logger.debug("canUseJsonFilesLastUpdate = {}", canUseJsonFilesLastUpdate);
	}
}
