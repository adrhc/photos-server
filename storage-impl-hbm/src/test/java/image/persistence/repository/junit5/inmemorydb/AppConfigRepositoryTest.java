package image.persistence.repository.junit5.inmemorydb;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.repository.AppConfigRepository;
import image.persistence.repository.junit5.springconfig.Junit5HbmInMemoryDbConfig;
import image.persistence.repository.junit5.springconfig.Junit5HbmInMemoryDbNestedConfig;
import image.persistence.repository.util.random.RandomBeansExtensionEx;
import io.github.glytching.junit.extension.random.Random;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.exparity.hamcrest.date.DateMatchers.sameOrBefore;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RandomBeansExtensionEx.class)
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
			AppConfig appConfig = randomInstance(false, AppConfig.class);
			AppConfigRepositoryTest.this.appConfigRepository.persist(appConfig);
			AppConfig dbAppConfig = AppConfigRepositoryTest.this
					.appConfigRepository.getById(appConfig.getId());
			assertTrue(dbAppConfig.similarTo(appConfig));
		}
	}

	@NotThreadSafe
	@Junit5HbmInMemoryDbNestedConfig
	class GetAppConfigById {
		private Integer idAppConfig;

		@BeforeEach
		void setUp() {
			AppConfig appConfig = entityAppConfigOf("byId", "byId-value");
			AppConfigRepositoryTest.this.appConfigRepository.persist(appConfig);
			this.idAppConfig = appConfig.getId();
		}

		@Test
		void getAppConfigById() {
			AppConfig appConfig = AppConfigRepositoryTest.this
					.appConfigRepository.getById(this.idAppConfig);
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
			randomInstanceStream(3, false, AppConfig.class)
					.peek(this.appConfigs::add)
					.peek(AppConfigRepositoryTest.this.appConfigRepository::persist)
					.forEach(ac -> ac.setValue(ac.getValue() + "-updated"));
		}

		@Test
		void update() {
			AppConfigRepositoryTest.this.appConfigRepository.saveAll(this.appConfigs);
			this.appConfigs.forEach(ac -> {
				AppConfig updatedAppConfig = AppConfigRepositoryTest.this
						.appConfigRepository.getById(ac.getId());
				assertEquals(ac.getValue(), updatedAppConfig.getValue());
			});
		}
	}

	/**
	 * Should be abstract class in order to run @Test as part of the derived class!
	 */
	abstract class UpdateValueTestBase {
		@Autowired
		AppConfigRepository appConfigRepository;
		AppConfig appConfig0;
		AppConfig appConfig1;

		@Test
		void updateValue() {
			this.appConfigRepository.updateValue(
					"updated-value", this.appConfig0.getId());
			AppConfig updatedAppConfig0 =
					this.appConfigRepository.getById(this.appConfig0.getId());
			AppConfig notUpdatedAppConfig1 =
					this.appConfigRepository.getById(this.appConfig1.getId());
			assertAll(
					() -> assertEquals("updated-value", updatedAppConfig0.getValue()),
					() -> assertEquals(this.appConfig1.getValue(), notUpdatedAppConfig1.getValue())
			);
		}
	}

	@NotThreadSafe
	@Junit5HbmInMemoryDbNestedConfig
	class UpdateValueTest1 extends UpdateValueTestBase {
		private List<AppConfig> appConfigs = new ArrayList<>();

		@BeforeEach
		void setUp() {
			randomInstanceStream(2, false, AppConfig.class)
					.peek(this.appConfigs::add)
					.forEach(this.appConfigRepository::persist);
			this.appConfig0 = this.appConfigs.get(0);
			this.appConfig1 = this.appConfigs.get(1);
		}
	}

	@NotThreadSafe
	@Junit5HbmInMemoryDbNestedConfig
	class UpdateValueTest2 extends UpdateValueTestBase {
		@Random(size = 2, type = AppConfig.class, excludes = {"id", "lastUpdate"})
		private List<AppConfig> appConfigs;

		@BeforeEach
		void setUp() {
			this.appConfigs.forEach(this.appConfigRepository::persist);
			this.appConfig0 = this.appConfigs.get(0);
			this.appConfig1 = this.appConfigs.get(1);
		}
	}

	@NotThreadSafe
	@Junit5HbmInMemoryDbNestedConfig
	class UpdateValueTest3 extends UpdateValueTestBase {
		private List<AppConfig> appConfigs = new ArrayList<>();

		@BeforeEach
		void setUp() {
			randomInstanceStream(2, false, AppConfig.class)
					.peek(this.appConfigs::add)
					.forEach(this.appConfigRepository::persist);
			this.appConfig0 = this.appConfigs.get(0);
			this.appConfig1 = this.appConfigs.get(1);
		}
	}

	@NotThreadSafe
	@Junit5HbmInMemoryDbNestedConfig
	class GetAppConfigByName {
		@BeforeAll
		void beforeAll() {
			AppConfigRepositoryTest.this.appConfigRepository
					.persist(entityAppConfigOf("byName", "byName-value"));
		}

		@Test
		void getAppConfigByName() {
			AppConfig appConfig = AppConfigRepositoryTest.this
					.appConfigRepository.findByName("byName");
			assertAll("getAppConfigByName",
					() -> assertEquals("byName", appConfig.getName()),
					() -> assertEquals("byName-value", appConfig.getValue()));
		}

		@Test
		void findByNameNotCached() {
			AppConfig appConfig = AppConfigRepositoryTest.this.appConfigRepository
					.findByNameNotCached("byName");
			assertAll("findByNameNotCached",
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
			this.appConfigRepository.persist(
					entityAppConfigOf(AppConfigEnum.albums_path, "/dummy-path"));
			this.appConfigRepository.persist(
					entityAppConfigOf(AppConfigEnum.photos_per_page, "10"));
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
					this.appConfigRepository.findAll();
			logger.debug("appConfigs.size = {}", appConfigs.size());
			// any persist add to the same in memory db instance
			assertThat(appConfigs.size(), greaterThanOrEqualTo(2));
		}

		@Test
		void findAllOrderByNameAscNotCached() {
			List<AppConfig> appConfigs = this.appConfigRepository
					.findAllOrderByNameAscNotCached();
			logger.debug("appConfigs.size = {}", appConfigs.size());
			// any persist add to the same in memory db instance
			assertThat(appConfigs.size(), greaterThanOrEqualTo(2));
		}

		@Test
		void getDBNow() {
			Date date = this.appConfigRepository.getDBNow();
			assertThat(date, sameOrBefore(new Date()));
		}
	}
}
