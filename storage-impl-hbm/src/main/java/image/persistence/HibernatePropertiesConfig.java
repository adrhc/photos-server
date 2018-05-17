package image.persistence;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;

@Configuration
public class HibernatePropertiesConfig {
	@Profile("in-memory-db")
	@Bean("hibernateProperties")
	public PropertiesFactoryBean hibernatePropertiesForInMemoryDb() {
		return buildPropertiesFactoryBean("hibernate-common.properties",
				"hibernate-in-memory.properties");
	}

	@Profile("test-jdbc-ds")
	@Bean("hibernateProperties")
	public PropertiesFactoryBean hibernatePropertiesForTestJdbcDs() {
		return buildPropertiesFactoryBean("hibernate-common.properties",
				"hibernate-stage-jdbc.properties");
	}

	@Profile({"prod-jdbc-ds", "prod-jndi-ds"})
	@Bean("hibernateProperties")
	public PropertiesFactoryBean hibernatePropertiesForJdbcDs() {
		return buildPropertiesFactoryBean("hibernate-common.properties",
				"hibernate-prod-jdbc.properties");
	}

	private PropertiesFactoryBean buildPropertiesFactoryBean(String... paths) {
		PropertiesFactoryBean pfb = new PropertiesFactoryBean();
		pfb.setIgnoreResourceNotFound(true);
		pfb.setLocations(Arrays.stream(paths)
				.map(ClassPathResource::new).toArray(ClassPathResource[]::new));
		return pfb;
	}
}
