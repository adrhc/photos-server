package image.hbm.repository.junit4.staging;

import image.hbm.repository.springconfig.HbmInMemoryDbConfig;
import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.repository.AppConfigRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by adr on 2/21/18.
 */
@RunWith(SpringRunner.class)
@HbmInMemoryDbConfig
@Category(HbmInMemoryDbConfig.class)
public class AppConfigRepositoryTest implements IAppConfigSupplier {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigRepositoryTest.class);

	@Autowired
	private AppConfigRepository appConfigRepository;

	private List<AppConfig> appConfigs;

	@Before
	public void setUp() {
		this.appConfigs = randomInstanceList(3, false, AppConfig.class);
		for (AppConfig appConfig : this.appConfigs) {
			this.appConfigRepository.persist(appConfig);
		}
	}

	@Test
	public void update() {
		for (AppConfig appConfig : this.appConfigs) {
			appConfig.setValue(appConfig.getValue() + "-updated");
		}
		this.appConfigRepository.saveAll(this.appConfigs);
		AppConfig updatedAppConfig;
		for (AppConfig appConfig : this.appConfigs) {
			updatedAppConfig = this.appConfigRepository
					.getById(appConfig.getId());
			Assert.assertEquals(updatedAppConfig.getValue(), appConfig.getValue());
		}
	}

	@After
	public void teardown() {
		this.appConfigs.forEach((ac) -> this.appConfigRepository.deleteById(ac.getId()));
	}
}
