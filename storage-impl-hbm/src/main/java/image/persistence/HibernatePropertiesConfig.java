package image.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
public class HibernatePropertiesConfig {
	@Autowired
	private Environment env;

	@Profile("in-memory-db")
	@Bean("hibernateProperties")
	public Properties hibernatePropertiesForInMemoryDb() {
		return new Properties() {
			{
				setProperty("hibernate.dialect",
						HibernatePropertiesConfig.this.env.getProperty("ramdb.hibernate.dialect"));

				// for hbm < 4x
//				setProperty("net.sf.ehcache.cacheManagerName",
//						env.getProperty("net.sf.ehcache.cacheManagerName.in_memory_db"));
				// solution for hbm >= 4x
//				setProperty("net.sf.ehcache.configurationResourceName",
//						env.getProperty("net.sf.ehcache.configurationResourceName.in_memory_db"));

				setProperty("hibernate.hbm2ddl.auto",
						HibernatePropertiesConfig.this.env.getProperty("ramdb.hibernate.hbm2ddl.auto"));
				addCommonHbmProps(this);
			}
		};
	}

	@Profile("test-jdbc-ds")
	@Bean("hibernateProperties")
	public Properties hibernatePropertiesForTestJdbcDs() {
		return new Properties() {
			{
				setProperty("hibernate.hbm2ddl.auto",
						HibernatePropertiesConfig.this.env.getProperty("test.hibernate.hbm2ddl.auto"));
				addCommonHbmProps(this);
			}
		};
	}

	@Profile({"prod-jdbc-ds", "prod-jndi-ds"})
	@Bean("hibernateProperties")
	public Properties hibernatePropertiesForJdbcDs() {
		return new Properties() {
			{
//				setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
				setProperty("hibernate.dialect.storage_engine",
						HibernatePropertiesConfig.this.env.getProperty("hibernate.dialect.storage_engine"));

				// for hbm < 4x
//				setProperty("net.sf.ehcache.cacheManagerName",
//						env.getProperty("net.sf.ehcache.cacheManagerName.jdbc_ds"));
				// solution for hbm >= 4x
//				setProperty("net.sf.ehcache.configurationResourceName",
//						env.getProperty("net.sf.ehcache.configurationResourceName.jdbc_ds"));

				addCommonHbmProps(this);
			}
		};
	}

	private void addCommonHbmProps(Properties properties) {
		properties.setProperty("hibernate.jdbc.batch_size",
				this.env.getProperty("hibernate.jdbc.batch_size"));
		properties.setProperty("hibernate.show_sql", this.env.getProperty("hibernate.show_sql"));
		properties.setProperty("hibernate.format_sql", this.env.getProperty("hibernate.format_sql"));
		properties.setProperty("hibernate.validator.autoregister_listeners",
				this.env.getProperty("hibernate.validator.autoregister_listeners"));

		// http://www.baeldung.com/hibernate-second-level-cache => for hibernate 5.x
		// http://docs.jboss.org/hibernate/orm/4.3/manual/en-US/html_single/#performance-cache
		// properties.setProperty("hibernate.generate_statistics", "true");
		// properties.setProperty("hibernate.cache.use_structured_entries", "true");

		properties.setProperty("hibernate.cache.use_second_level_cache",
				this.env.getProperty("hibernate.cache.use_second_level_cache"));
		properties.setProperty("hibernate.cache.use_query_cache",
				this.env.getProperty("hibernate.cache.use_query_cache"));
		properties.setProperty("hibernate.cache.region.factory_class",
				this.env.getProperty("hibernate.cache.region.factory_class"));
//		properties.setProperty("net.sf.ehcache.configurationResourceName",
//				env.getProperty("net.sf.ehcache.configurationResourceName"));

		// properties.setProperty("hibernate.hbm2ddl.auto", "update");
		// properties.setProperty("hibernate.id.new_generator_mappings", "true");
		// properties.setProperty("hibernate.current_session_context_class", "jta");
		// properties.setProperty("javax.persistence.validation.mode", "");

	}
}
