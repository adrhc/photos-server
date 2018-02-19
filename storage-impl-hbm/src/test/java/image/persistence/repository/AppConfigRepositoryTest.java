package image.persistence.repository;

import image.persistence.DataSourceConfig;
import image.persistence.HibernateConfig;
import image.persistence.entity.AppConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by adr on 2/19/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {DataSourceConfig.class, HibernateConfig.class})
//@TestPropertySource(properties = {"jndi = dummy"})
@ActiveProfiles("test")
public class AppConfigRepositoryTest {
	@Autowired
	private AppConfigRepository appConfigRepository;

	@Test
	public void getAppConfigs() {
		List<AppConfig> appConfigs = appConfigRepository.getAppConfigs();
		Assert.assertFalse(appConfigs.isEmpty());
	}
}
