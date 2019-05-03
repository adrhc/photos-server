package image.jpa2xtests.repositories;

import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2x.repositories.AppConfigRepository;
import image.jpa2xtests.config.Junit5Jpa2xInMemoryDbConfig;
import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.entitytests.IAppConfigSupplier;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RandomBeansExtensionEx.class)
@Junit5Jpa2xInMemoryDbConfig
@Slf4j
class AppConfigRepositoryTest implements IAppConfigSupplier {
	@Inject
	private AppConfigRepository appConfigRepository;

	@Test
	void getDBNow() {
		Date dbNow = this.appConfigRepository.getDBNow();
		assertThat("getDBNow", dbNow, Matchers.lessThanOrEqualTo(new Date()));
	}

	@Junit5Jpa2xInMemoryDbConfig
	@Nested
	class UpdateValue {
		private AppConfig appConfig;

		@BeforeAll
		void beforeAll() {
			this.appConfig = AppConfigRepositoryTest.this.appConfigRepository
					.save(entityAppConfigOf("byName", "byName-value"));
		}

		@AfterAll
		void afterAll() {
			AppConfigRepositoryTest.this.appConfigRepository.deleteAllInBatch();
		}

		@Test
		void updateValue() {
			this.appConfig.setValue(this.appConfig.getValue() + "-updated");
			AppConfigRepositoryTest.this.appConfigRepository
					.updateValue(this.appConfig.getValue(), this.appConfig.getId());
			AppConfig dbAppConfig = AppConfigRepositoryTest.this.appConfigRepository.getById(this.appConfig.getId());
			assertEquals(this.appConfig.getValue(), dbAppConfig.getValue(), "getAppConfigByName");
		}
	}

	@Junit5Jpa2xInMemoryDbConfig
	@Nested
	class DeleteByEnumeratedName {
		@PersistenceContext
		private EntityManager em;

		@BeforeAll
		void beforeAll() {
			AppConfigRepositoryTest.this.appConfigRepository
					.persist(entityAppConfigOf(AppConfigEnum.albums_path, "albums_path-value"));
		}

		@AfterAll
		void afterAll() {
			AppConfigRepositoryTest.this.appConfigRepository.deleteAllInBatch();
		}

		@Test
		void deleteByEnumeratedName() {
			// testing the cache
			Cache cache = this.em.getEntityManagerFactory().getCache();
			assertFalse(cache.contains(AppConfig.class, 1), "AppConfig:1 already cached!");
			List<AppConfig> all = AppConfigRepositoryTest.this.appConfigRepository.findAll();
			log.debug("AppConfig: {}", all.get(0).toString());
			assertThat("Too many AppConfig in DB!", all, hasSize(1));
			assertTrue(cache.contains(AppConfig.class, 1), "AppConfig:1 not cached!");
			// testing deleteByEnumeratedName
			AppConfigRepositoryTest.this.appConfigRepository
					.deleteByEnumeratedName(AppConfigEnum.albums_path);
			all = AppConfigRepositoryTest.this.appConfigRepository.findAll();
			assertThat("Some AppConfig still exist!", all, empty());
		}
	}

	@Junit5Jpa2xInMemoryDbConfig
	@Nested
	class VariousFinders {
		@BeforeAll
		void beforeAll() {
			AppConfigRepositoryTest.this.appConfigRepository
					.persist(entityAppConfigOf("byName", "byName-value"));
			AppConfigRepositoryTest.this.appConfigRepository
					.persist(entityAppConfigOf(AppConfigEnum.photos_per_page, "120"));
			AppConfigRepositoryTest.this.appConfigRepository
					.persist(entityAppConfigOf(AppConfigEnum.albums_path, "albums_path-value"));
		}

		@AfterAll
		void afterAll() {
			AppConfigRepositoryTest.this.appConfigRepository.deleteAllInBatch();
		}

		@Test
		void getAppConfigByName() {
			AppConfig appConfig = AppConfigRepositoryTest
					.this.appConfigRepository.findByName("byName");
			assertAll("getAppConfigByName",
					() -> assertEquals("byName", appConfig.getName()),
					() -> assertEquals("byName-value", appConfig.getValue()));
		}

		@Test
		void findByNameNotCached() {
			AppConfig appConfig = AppConfigRepositoryTest
					.this.appConfigRepository.findByNameNotCached("byName");
			assertAll("findByNameNotCached",
					() -> assertEquals("byName", appConfig.getName()),
					() -> assertEquals("byName-value", appConfig.getValue()));
		}

		@Test
		void findAllOrderByNameAscNotCached() {
			List<AppConfig> appConfigs = AppConfigRepositoryTest
					.this.appConfigRepository.findAllOrderByNameAscNotCached();
			assertThat("findAllOrderByNameAscNotCached", appConfigs.size(), is(3));
		}

		@Test
		void findValueByEnumeratedName() {
			String value = AppConfigRepositoryTest
					.this.appConfigRepository.findValueByEnumeratedName(AppConfigEnum.albums_path);
			assertEquals("albums_path-value", value, "findValueByEnumeratedName");
		}

		@Test
		void getAlbumsPath() {
			String value = AppConfigRepositoryTest
					.this.appConfigRepository.getAlbumsPath();
			assertEquals("albums_path-value", value, "getAlbumsPath");
		}

		@Test
		void getPhotosPerPage() {
			Integer value = AppConfigRepositoryTest
					.this.appConfigRepository.getPhotosPerPage();
			assertEquals((Integer) 120, value, "getPhotosPerPage");
		}
	}
}
