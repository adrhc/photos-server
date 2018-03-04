package image.persistence.repository.junit5;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.repository.AppConfigRepository;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@NotThreadSafe
@Junit5HbmInMemoryDbConfig
class AppConfigRepositoryTest implements IAppConfigSupplier {
	@Autowired
	private AppConfigRepository appConfigRepository;

	@RepeatedTest(5)
	void createAppConfig() {
		AppConfig appConfig = supplyAppConfig();
		this.appConfigRepository.createAppConfig(appConfig);
		AppConfig dbAppConfig = this.appConfigRepository.getAppConfigById(appConfig.getId());
		assertTrue(dbAppConfig.similarTo(appConfig));
	}

	@NotThreadSafe
	@Junit5HbmInMemoryDbNestedConfig
	class GetAppConfigById {
		private Integer idAppConfig;

		@BeforeEach
		void setUp() {
			AppConfig appConfig = supplyAppConfig("byId", "byId-value");
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
			IntStream.range(0, 5).boxed().map(i -> supplyAppConfig())
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
		@BeforeEach
		void setUp() {
			AppConfigRepositoryTest.this.appConfigRepository
					.createAppConfig(supplyAppConfig("byName", "byName-value"));
		}

		@Test
		void getAppConfigByName() {
			AppConfig appConfig = AppConfigRepositoryTest.this
					.appConfigRepository.getAppConfigByName("byName");
			assertAll("getAppConfigByName",
					() -> assertEquals("byName", appConfig.getName()),
					() -> assertEquals("byName-value", appConfig.getValue()));
		}
	}

	@NotThreadSafe
	@Junit5HbmInMemoryDbNestedConfig
	class AppConfigRUDTest {
		@BeforeAll
		void beforeAll() {
			AppConfigRepositoryTest.this.appConfigRepository.createAppConfig(
					supplyAppConfig(AppConfigEnum.albums_path.getValue(), "/dummy-path"));
			AppConfigRepositoryTest.this.appConfigRepository.createAppConfig(
					supplyAppConfig(AppConfigEnum.photos_per_page.getValue(), "10"));
		}

		@Test
		void getPhotosPerPage() {
			Integer photosPerPage = AppConfigRepositoryTest.this.appConfigRepository.getPhotosPerPage();
			assertThat(photosPerPage, equalTo(10));
		}

		@Test
		void getAlbumsPath() {
			String albumsPath = AppConfigRepositoryTest.this.appConfigRepository.getAlbumsPath();
			assertEquals("/dummy-path", albumsPath);
		}

		@Test
		void getAppConfigs() {
			List<AppConfig> appConfigs =
					AppConfigRepositoryTest.this.appConfigRepository.getAppConfigs();
			assertThat(appConfigs.size(), equalTo(7));
		}

		@Test
		void testGetNoCacheableOrderedAppConfigs() {
		}

		@Test
		void testGetNoCacheableAppConfigByName() {
		}

		@Test
		void getDBNow() {
		}
	}
}