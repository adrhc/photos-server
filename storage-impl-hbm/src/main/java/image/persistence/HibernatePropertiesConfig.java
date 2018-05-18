package image.persistence;

import exifweb.util.PropertiesFactoryBeanEx;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class HibernatePropertiesConfig {
	@Profile("in-memory-db")
	@Bean("hibernateProperties")
	public PropertiesFactoryBean hibernatePropertiesForInMemoryDb() {
		return new PropertiesFactoryBeanEx("hibernate/hibernate-common.properties",
				"hibernate/hibernate-in-memory.properties");
	}

	@Profile("test-jdbc-ds")
	@Bean("hibernateProperties")
	public PropertiesFactoryBean hibernatePropertiesForTestJdbcDs() {
		return new PropertiesFactoryBeanEx("hibernate/hibernate-common.properties",
				"hibernate/hibernate-stage-jdbc.properties");
	}

	@Profile({"prod-jdbc-ds", "prod-jndi-ds"})
	@Bean("hibernateProperties")
	public PropertiesFactoryBean hibernatePropertiesForJdbcDs() {
		return new PropertiesFactoryBeanEx("hibernate/hibernate-common.properties",
				"hibernate/hibernate-prod-jdbc.properties");
	}
}
