package image.persistence.repository.junit5;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.repository.AppConfigRepository;
import image.persistence.repository.springtestconfig.InMemoryDbTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@NotThreadSafe
@ExtendWith(SpringExtension.class)
@InMemoryDbTestConfig
@Tag("junit5")
@Tag("hbm")
@Tag("inmemorydb")
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

	@Test
	void getPhotosPerPage() {
	}

	@Test
	void getLinuxAlbumPath() {
	}

	@Test
	void getAppConfigById() {
	}

	@Test
	void getAppConfigByName() {
	}

	@Test
	void update() {
	}

	@Test
	void getAppConfigs() {
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