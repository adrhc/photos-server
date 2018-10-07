package image.persistence;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

@Configuration
public class HibernatePropertiesConfig {
	@Inject
	private Environment ev;

	@Profile("in-memory-db")
	@Bean("hibernateProperties")
	public PropertiesFactoryBean hibernatePropertiesForInMemoryDb() {
		return propsFrom("hibernate-common", "hibernate-in-memory");
	}

	@Profile("test-jdbc-ds")
	@Bean("hibernateProperties")
	public PropertiesFactoryBean hibernatePropertiesForTestJdbcDs() {
		return propsFrom("hibernate-common", "hibernate-stage-jdbc");
	}

	@Profile({"prod-jdbc-ds", "prod-jndi-ds"})
	@Bean("hibernateProperties")
	public PropertiesFactoryBean hibernatePropertiesForJdbcDs() {
		return propsFrom("hibernate-common", "hibernate-prod-jdbc");
	}

	private PropertiesFactoryBean propsFrom(String... paths) {
		Resource[] locations =
				Arrays.stream(paths)
						.map(s -> "hibernate/" + s + ".properties")
						.map(ClassPathResource::new)
						.toArray(ClassPathResource[]::new);
		PropertiesFactoryBean properties = new PropertiesFactoryBean();
		// allows for @TestPropertySource overrides
//		properties.setLocalOverride(true);
		properties.setLocations(locations);
		try {
			// force to load the properties
			properties.afterPropertiesSet();
			// @TestPropertySource support
			properties.setProperties(envProps(properties.getObject().stringPropertyNames()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return properties;
	}

	private Properties envProps(Collection<String> keys) {
		Properties p = new Properties();
		keys.stream().map(key -> new String[]{key, this.ev.getProperty(key)})
				.filter(kvTuple -> kvTuple[1] != null)
				.forEach(kvTuple -> p.setProperty(kvTuple[0], kvTuple[1]));
		return p;
	}
}
