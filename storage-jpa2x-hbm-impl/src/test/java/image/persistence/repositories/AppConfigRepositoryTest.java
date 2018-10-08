package image.persistence.repositories;

import image.persistence.config.Junit5Jpa2xInMemoryDbConfig;
import image.persistence.config.NestedPerClass;
import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.repository.util.random.RandomBeansExtensionEx;
import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.NotThreadSafe;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RandomBeansExtensionEx.class)
@NotThreadSafe
@Junit5Jpa2xInMemoryDbConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class AppConfigRepositoryTest implements IAppConfigSupplier {
	@Inject
	private AppConfigRepository appConfigRepository;

	@Test
	void getDBNow() {
		Date dbNow = this.appConfigRepository.getDBNow();
		assertThat("getDBNow", dbNow, Matchers.lessThanOrEqualTo(new Date()));
	}

	@NestedPerClass
	@Junit5Jpa2xInMemoryDbConfig
	class UpdateValue {
		private AppConfig appConfig;

		@BeforeAll
		void beforeAll() {
			this.appConfig = AppConfigRepositoryTest.this.appConfigRepository
					.save(entityAppConfigOf("byName", "byName-value"));
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

	@NestedPerClass
	@Junit5Jpa2xInMemoryDbConfig
	class VariousFinders {
		@BeforeAll
		void beforeAll() {
			AppConfigRepositoryTest.this.appConfigRepository
					.persist(entityAppConfigOf("byName", "byName-value"));
			AppConfigRepositoryTest.this.appConfigRepository
					.persist(entityAppConfigOf(AppConfigEnum.albums_path, "albums_path-value"));
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
			assertThat("findAllOrderByNameAscNotCached", appConfigs.size(), is(2));
		}

		@Test
		void findByEnumeratedName() {
			String value = AppConfigRepositoryTest
					.this.appConfigRepository.findByEnumeratedName(AppConfigEnum.albums_path);
			assertEquals("albums_path-value", value, "findByEnumeratedName");
		}
	}
}
