package image.persistence.repository;

import image.persistence.HibernateConfig;
import image.persistence.entity.AppConfig;
import image.persistence.repository.springprofile.JdbcDsActivated;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by adr on 2/21/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {HibernateConfig.class})
@JdbcDsActivated
@Category(HibernateConfig.class)
public class AppConfigRepositoryUpdateTest {
	@Autowired
	private AppConfigRepository appConfigRepository;

	private List<AppConfig> appConfigs;

	@Before
	public void setUp() {
		appConfigs = appConfigRepository.getAppConfigs();
	}

	@Ignore("todo: use in memory database")
	@Test
	public void update() {
		appConfigRepository.update(appConfigs);
	}
}
