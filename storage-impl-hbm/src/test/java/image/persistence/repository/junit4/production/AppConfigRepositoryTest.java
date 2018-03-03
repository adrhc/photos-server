package image.persistence.repository.junit4.production;

import image.persistence.entity.AppConfig;
import image.persistence.repository.AppConfigRepository;
import image.persistence.repository.springtestconfig.ProdJdbcDsTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by adr on 2/19/18.
 */
@RunWith(SpringRunner.class)
@NotThreadSafe
@ProdJdbcDsTestConfig
@Category(ProdJdbcDsTestConfig.class)
public class AppConfigRepositoryTest {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigRepositoryTest.class);

	@Autowired
	private AppConfigRepository appConfigRepository;

	@Test
	public void getLinuxAlbumPath() {
		String linuxAlbumPath = this.appConfigRepository.getAlbumsPath();
		assertThat(linuxAlbumPath, notNullValue());
		logger.debug("linuxAlbumPath: {}", linuxAlbumPath);
	}

	@Test
	public void getPhotosPerPage() {
		Integer photosPerPage = this.appConfigRepository.getPhotosPerPage();
		assertThat(photosPerPage, greaterThan(0));
		logger.debug("photosPerPage = {}", photosPerPage);
	}

	@Test
	public void getAppConfigById() {
		AppConfig appConfig = this.appConfigRepository.getAppConfigById(1);
		assertThat(appConfig, notNullValue());
		logger.debug(appConfig.toString());
	}

	@Test
	public void getAppConfigByName() {
		AppConfig appConfig = this.appConfigRepository.getAppConfigByName("albums_path");
		Assert.assertEquals(appConfig.getValue(),
				"/home/adr/Pictures/FOTO Daniela & Adrian jpeg/albums");
		logger.debug(appConfig.toString());
	}

	@Test
	public void testGetNoCacheableAppConfigByName() {
		AppConfig appConfig = this.appConfigRepository.testGetNoCacheableAppConfigByName("albums_path");
		assertThat(appConfig, notNullValue());
		logger.debug(appConfig.toString());
	}

	@Test
	public void getAppConfigs() {
		List<AppConfig> appConfigs = this.appConfigRepository.getAppConfigs();
		assertThat(appConfigs, hasSize(greaterThan(0)));
		logger.debug("appConfigs.size = {}", appConfigs.size());
//		logger.debug(appConfigs.stream().map(AppConfig::toString)
//				.collect(Collectors.joining("\n")));
	}

	@Test
	public void testGetNoCacheableOrderedAppConfigs() {
		List<AppConfig> appConfigs = this.appConfigRepository.testGetNoCacheableOrderedAppConfigs();
		assertThat(appConfigs, hasItem(anything()));
		logger.debug("appConfigs.size = {}", appConfigs.size());
//		logger.debug(appConfigs.stream().map(AppConfig::toString)
//				.collect(Collectors.joining("\n")));
	}

	@Test
	public void getDBNow() {
		Date date = this.appConfigRepository.getDBNow();
		assertThat(date, notNullValue());
		logger.debug(date.toString());
	}
}
