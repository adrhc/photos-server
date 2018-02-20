package image.persistence.integration;

import image.persistence.HibernateConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Using TestPropertySource
 * <p>
 * Created by adr on 2/19/18.
 */
@Configuration
@PropertySource("classpath:/jdbc-datasource.properties")
@Import(HibernateConfig.class)
@Profile("integration-tests")
public class TestHibernateConfig {
}
