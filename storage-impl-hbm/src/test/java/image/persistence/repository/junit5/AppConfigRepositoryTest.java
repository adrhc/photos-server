package image.persistence.repository.junit5;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.repository.AppConfigRepository;
import image.persistence.repository.junit5.testconfig.Junit5HbmInMemoryDbConfig;
import image.persistence.repository.junit5.testconfig.Junit5HbmInMemoryDbNestedConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static org.exparity.hamcrest.date.DateMatchers.sameOrBefore;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.jupiter.api.Assertions.*;

@NotThreadSafe
@Junit5HbmInMemoryDbConfig
class AppConfigRepositoryTest implements IAppConfigSupplier {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigRepositoryTest.class);
	@Autowired
	private AppConfigRepository appConfigRepository;

	@NotThreadSafe
	@Junit5HbmInMemoryDbNestedConfig
	class CreateAppConfig {
		@RepeatedTest(3)
		void createAppConfig() {
			AppConfig appConfig = supplyEntityAppConfig();
			AppConfigRepositoryTest.this.appConfigRepository.createAppConfig(appConfig);
			AppConfig dbAppConfig = AppConfigRepositoryTest.this
					.appConfigRepository.getAppConfigById(appConfig.getId());
			assertTrue(dbAppConfig.similarTo(appConfig));
		}
	}

	@NotThreadSafe
	@Junit5HbmInMemoryDbNestedConfig
	class GetAppConfigById {
		private Integer idAppConfig;

		@BeforeEach
		void setUp() {
			AppConfig appConfig = supplyEntityAppConfig("byId", "byId-value");
			AppConfigRepositoryTest.this.appConfigRepository.createAppConfig(appConfig);
			this.idAppConfig = appConfig.getId();
		}

		@Test
		void getAppConfigById() {
			AppConfig appConfig = AppConfigRepositoryTest.this
					.appConfigRepository.getAppConfigById(this.idAppConfig);
			assertAll("getAppConfigById",
					() -> assertEquals("byId", appConfig.getName()),
					() -> assertEquals("byId-value", appConfig.getValue()));
		}
	}

	@NotThreadSafe
	@Junit5HbmInMemoryDbNestedConfig
	class UpdateTest {
		private List<AppConfig> appConfigs = new ArrayList<>();

		@BeforeEach
		void setUp() {
			IntStream.range(0, 3).boxed().map(i -> supplyEntityAppConfig())
					.peek(this.appConfigs::add)
					.peek(AppConfigRepositoryTest.this.appConfigRepository::createAppConfig)
					.forEach(ac -> ac.setValue(ac.getValue() + "-updated"));
		}

		@Test
		void update() {
			AppConfigRepositoryTest.this.appConfigRepository.update(this.appConfigs);
			this.appConfigs.forEach(ac -> {
				AppConfig updatedAppConfig = AppConfigRepositoryTest.this
						.appConfigRepository.getAppConfigById(ac.getId());
				assertEquals(ac.getValue(), updatedAppConfig.getValue());
			});
		}
	}

	@NotThreadSafe
	@Junit5HbmInMemoryDbNestedConfig
	class GetAppConfigByName {
		@BeforeAll
		void beforeAll() {
			AppConfigRepositoryTest.this.appConfigRepository
					.createAppConfig(supplyEntityAppConfig("byName", "byName-value"));
		}

		@Test
		void getAppConfigByName() {
			AppConfig appConfig = AppConfigRepositoryTest.this
					.appConfigRepository.getAppConfigByName("byName");
			assertAll("getAppConfigByName",
					() -> assertEquals("byName", appConfig.getName()),
					() -> assertEquals("byName-value", appConfig.getValue()));
		}

		@Test
		void testGetNoCacheableAppConfigByName() {
			AppConfig appConfig = AppConfigRepositoryTest.this.appConfigRepository
					.testGetNoCacheableAppConfigByName("byName");
			assertAll("testGetNoCacheableAppConfigByName",
					() -> assertEquals("byName", appConfig.getName()),
					() -> assertEquals("byName-value", appConfig.getValue()));
		}
	}

	@NotThreadSafe
	@Junit5HbmInMemoryDbNestedConfig
	class AppConfigOtherTest {
		@Autowired
		private AppConfigRepository appConfigRepository;

		@BeforeAll
		void beforeAll() {
			this.appConfigRepository.createAppConfig(
					supplyEntityAppConfig(AppConfigEnum.albums_path.getValue(), "/dummy-path"));
			this.appConfigRepository.createAppConfig(
					supplyEntityAppConfig(AppConfigEnum.photos_per_page.getValue(), "10"));
		}

		@Test
		void getPhotosPerPage() {
			Integer photosPerPage = this.appConfigRepository.getPhotosPerPage();
			assertThat(photosPerPage, equalTo(10));
		}

		@Test
		void getAlbumsPath() {
			String albumsPath = this.appConfigRepository.getAlbumsPath();
			assertEquals("/dummy-path", albumsPath);
		}

		@Test
		void getAppConfigs() {
			List<AppConfig> appConfigs =
					this.appConfigRepository.getAppConfigs();
			logger.debug("appConfigs.size = {}", appConfigs.size());
			assertThat(appConfigs.size(), isOneOf(2, 5));
		}

		@Test
		void testGetNoCacheableOrderedAppConfigs() {
			List<AppConfig> appConfigs = this.appConfigRepository
					.testGetNoCacheableOrderedAppConfigs();
			logger.debug("appConfigs.size = {}", appConfigs.size());
			assertThat(appConfigs.size(), isOneOf(2, 5));
		}

		@Test
		void getDBNow() {
			Date date = this.appConfigRepository.getDBNow();
			assertThat(date, sameOrBefore(new Date()));
		}
	}
}
