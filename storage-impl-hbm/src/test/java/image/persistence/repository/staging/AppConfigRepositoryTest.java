package image.persistence.repository.staging;

import image.persistence.entity.AppConfig;
import image.persistence.entity.IAppConfigSupplier;
import image.persistence.repository.AppConfigRepository;
import image.persistence.repository.springtestconfig.InMemoryDbTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Don't delete created configs with an have @After because I use @InMemoryDbTestConfig.
 * <p>
 * Created by adr on 2/21/18.
 */
@RunWith(SpringRunner.class)
@NotThreadSafe
@InMemoryDbTestConfig
@Category(InMemoryDbTestConfig.class)
public class AppConfigRepositoryTest implements IAppConfigSupplier {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigRepositoryTest.class);

	@Autowired
	private AppConfigRepository appConfigRepository;

	private List<AppConfig> appConfigs;

	@Before
	public void setUp() {
		this.appConfigs = new ArrayList<>();
		this.appConfigs.add(supplyAppConfig());
		this.appConfigs.add(supplyAppConfig());
		this.appConfigs.add(supplyAppConfig());
		for (AppConfig appConfig : this.appConfigs) {
			this.appConfigRepository.createAppConfig(appConfig);
		}
	}

	@Test
	public void update() {
		for (AppConfig appConfig : this.appConfigs) {
			appConfig.setValue(appConfig.getValue() + "-updated");
		}
		this.appConfigRepository.update(this.appConfigs);
		AppConfig updatedAppConfig;
		for (AppConfig appConfig : this.appConfigs) {
			updatedAppConfig = this.appConfigRepository
					.getAppConfigById(appConfig.getId());
			Assert.assertEquals(updatedAppConfig.getValue(), appConfig.getValue());
		}
		logger.debug("appConfigs updated");
	}
}
