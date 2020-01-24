package image.jpa2x;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

@Configuration
public class HibernatePropertiesConfig {
	@Autowired
	private Environment ev;

	@Profile("in-memory-db")
	@Bean("hibernateProperties")
	public PropertiesFactoryBean hibernatePropertiesForInMemoryDb() {
		return this.propsFrom("hibernate-common", "hibernate-in-memory");
	}

	@Profile("test-jdbc-ds")
	@Bean("hibernateProperties")
	public PropertiesFactoryBean hibernatePropertiesForTestJdbcDs() {
		return this.propsFrom("hibernate-common", "hibernate-stage-jdbc");
	}

	@Profile({"prod-jdbc-ds", "prod-jndi-ds"})
	@Bean("hibernateProperties")
	public PropertiesFactoryBean hibernatePropertiesForJdbcDs() {
		return this.propsFrom("hibernate-common", "hibernate-prod-jdbc");
	}

	private PropertiesFactoryBean propsFrom(String... paths) {
		PropertiesFactoryBean factoryBean = new PropertiesFactoryBean();
		Resource[] locations =
				Arrays.stream(paths)
						.map(s -> "hibernate/" + s + ".properties")
						.map(ClassPathResource::new)
						.toArray(ClassPathResource[]::new);
		factoryBean.setLocations(locations);
		try {
			// force to load the properties
			factoryBean.afterPropertiesSet();
			// allow for @TestPropertySource to overwrite hibernate-*.properties
			this.keepEnvProps(factoryBean.getObject());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Properties properties;
		try {
			properties = factoryBean.getObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		factoryBean = new PropertiesFactoryBean();
		factoryBean.setProperties(properties);
		return factoryBean;
	}

	/**
	 * @TestPropertySource support
	 */
	private void keepEnvProps(Properties p) {
		p.stringPropertyNames().stream()
				.map(key -> new String[]{key, this.ev.getProperty(key)})
				.filter(kvTuple -> kvTuple[1] != null)
				.forEach(kvTuple -> p.setProperty(kvTuple[0], kvTuple[1]));
	}
}
