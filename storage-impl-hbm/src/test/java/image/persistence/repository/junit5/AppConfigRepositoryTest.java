package image.persistence.repository.junit5;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.repository.AppConfigRepository;
import image.persistence.repository.springtestconfig.InMemoryDbTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NotThreadSafe
@ExtendWith(SpringExtension.class)
@InMemoryDbTestConfig
@Tag("junit5")
@Tag("hbm")
@Tag("inmemorydb")
class AppConfigRepositoryTest implements IAppConfigSupplier {
	@Autowired
	private AppConfigRepository appConfigRepository;

	@Test
	void createAppConfig() {
		List<AppConfig> appConfigs = new ArrayList<>();
		appConfigs.add(supplyAppConfig());
		appConfigs.add(supplyAppConfig());
		appConfigs.add(supplyAppConfig());
		appConfigs.forEach(this.appConfigRepository::createAppConfig);
		this.appConfigRepository.getAppConfigs().forEach(appConfig ->
				assertEquals(1,
						appConfigs.stream().filter(appConfig::similarTo).count()));
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