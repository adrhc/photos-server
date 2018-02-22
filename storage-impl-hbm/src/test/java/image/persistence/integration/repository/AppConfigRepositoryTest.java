package image.persistence.integration.repository;

import image.persistence.HibernateConfig;
import image.persistence.entity.AppConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * Created by adr on 2/19/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {HibernateConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=false")
@ActiveProfiles({"jdbc-ds"})
@Category(HibernateConfig.class)
public class AppConfigRepositoryTest {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigRepositoryTest.class);

	@Autowired
	private AppConfigRepository appConfigRepository;

	@Test
	public void getLinuxAlbumPath() {
		String linuxAlbumPath = appConfigRepository.getLinuxAlbumPath();
		assertThat(linuxAlbumPath, notNullValue());
		logger.debug("linuxAlbumPath: {}", linuxAlbumPath);
	}

	@Test
	public void getPhotosPerPage() {
		Integer photosPerPage = appConfigRepository.getPhotosPerPage();
		assertThat(photosPerPage, greaterThan(0));
		logger.debug("photosPerPage = {}", photosPerPage);
	}

	@Test
	public void getAppConfigById() {
		AppConfig appConfig = appConfigRepository.getAppConfigById(1);
		assertThat(appConfig, notNullValue());
		logger.debug(appConfig.toString());
	}

	@Test
	public void getAppConfigByName() {
		AppConfig appConfig = appConfigRepository.getAppConfigByName("albums_path");
		assertThat(appConfig, notNullValue());
		logger.debug(appConfig.toString());
	}

	@Test
	public void testGetNoCacheableAppConfigByName() {
		AppConfig appConfig = appConfigRepository.testGetNoCacheableAppConfigByName("albums_path");
		assertThat(appConfig, notNullValue());
		logger.debug(appConfig.toString());
	}

	@Test
	public void getAppConfigs() {
		List<AppConfig> appConfigs = appConfigRepository.getAppConfigs();
		assertThat(appConfigs, hasSize(greaterThan(0)));
		logger.debug(appConfigs.stream().map(AppConfig::toString)
				.collect(Collectors.joining("\n")));
	}

	@Test
	public void testGetNoCacheableOrderedAppConfigs() {
		List<AppConfig> appConfigs = appConfigRepository.testGetNoCacheableOrderedAppConfigs();
		assertThat(appConfigs, hasItem(anything()));
		logger.debug(appConfigs.stream().map(AppConfig::toString)
				.collect(Collectors.joining("\n")));
	}

	@Test
	public void getDBNow() {
		Date date = appConfigRepository.getDBNow();
		assertThat(date, notNullValue());
		logger.debug(date.toString());
	}
}
