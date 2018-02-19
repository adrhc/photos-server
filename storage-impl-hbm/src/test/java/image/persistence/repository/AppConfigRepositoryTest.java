package image.persistence.repository;

import image.persistence.TestConfig;
import image.persistence.entity.AppConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;


/**
 * Created by adr on 2/19/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource(properties = "jndi.name=dummy")
@ActiveProfiles("test")
public class AppConfigRepositoryTest {
	@Autowired
	private AppConfigRepository appConfigRepository;

	@Test
	public void getAppConfigs() {
		List<AppConfig> appConfigs = appConfigRepository.getAppConfigs();
		Assert.assertFalse(appConfigs.isEmpty());
		assertThat(appConfigs, hasSize(greaterThan(0)));
	}
}
