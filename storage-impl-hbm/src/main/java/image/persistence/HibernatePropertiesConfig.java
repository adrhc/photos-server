package image.persistence;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Arrays;

@Configuration
public class HibernatePropertiesConfig {
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
		properties.setLocations(locations);
		return properties;
	}
}
