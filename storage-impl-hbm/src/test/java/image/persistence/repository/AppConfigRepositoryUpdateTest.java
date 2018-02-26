package image.persistence.repository;

import image.persistence.HibernateConfig;
import image.persistence.entity.AppConfig;
import image.persistence.repository.springtestconfig.ProdJdbcDsTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Before;
import org.junit.Ignore;
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
@NotThreadSafe
@ProdJdbcDsTestConfig
@Category(HibernateConfig.class)
public class AppConfigRepositoryUpdateTest {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigRepositoryUpdateTest.class);

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
		logger.debug("appConfigs updated");
	}
}
