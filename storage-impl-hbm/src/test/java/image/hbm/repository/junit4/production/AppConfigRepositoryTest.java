package image.hbm.repository.junit4.production;

import image.hbm.repository.springconfig.HbmProdJdbcDbConfig;
import image.persistence.entity.AppConfig;
import image.persistence.repository.AppConfigRepository;
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

import static org.exparity.hamcrest.date.DateMatchers.sameOrBefore;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by adr on 2/19/18.
 */
@RunWith(SpringRunner.class)
@HbmProdJdbcDbConfig
@Category(HbmProdJdbcDbConfig.class)
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
		AppConfig appConfig = this.appConfigRepository.getById(1);
		assertThat(appConfig, notNullValue());
		logger.debug(appConfig.toString());
	}

	@Test
	public void getAppConfigByName() {
		AppConfig appConfig = this.appConfigRepository.findByName("albums_path");
		Assert.assertEquals("/fast-disk/FOTO Daniela & Adrian jpeg/albums", appConfig.getValue());
		logger.debug(appConfig.toString());
	}

	@Test
	public void findByNameNotCached() {
		AppConfig appConfig = this.appConfigRepository.findByNameNotCached("albums_path");
		assertThat(appConfig, notNullValue());
		logger.debug(appConfig.toString());
	}

	@Test
	public void getAppConfigs() {
		List<AppConfig> appConfigs = this.appConfigRepository.findAll();
		assertThat(appConfigs, hasSize(greaterThan(0)));
		logger.debug("appConfigs.size = {}", appConfigs.size());
//		logger.debug(appConfigs.stream().map(AppConfig::toString)
//				.collect(Collectors.joining("\n")));
	}

	@Test
	public void findAllOrderByNameAscNotCached() {
		List<AppConfig> appConfigs = this.appConfigRepository.findAllOrderByNameAscNotCached();
		assertThat(appConfigs, hasItem(anything()));
		logger.debug("appConfigs.size = {}", appConfigs.size());
//		logger.debug(appConfigs.stream().map(AppConfig::toString)
//				.collect(Collectors.joining("\n")));
	}

	@Test
	public void getDBNow() {
		Date date = this.appConfigRepository.getDBNow();
		assertThat(date, sameOrBefore(new Date()));
		logger.debug(date.toString());
	}
}
