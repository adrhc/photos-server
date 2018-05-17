package image.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import exifweb.util.PropertiesFactoryBeanEx;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DataSourceConfig {
	/**
	 * SQLErrorCodeSQLExceptionTranslator (uses sql-error-codes.xml) -> for jdbc only?
	 * <p>
	 * <jee:jndi-lookup id="dataSource" jndi-name="${jndi.name}" />
	 * <p>
	 * In tomcat's context.xml define: <Resource ... />
	 */
	@Profile("prod-jndi-ds")
	@Bean
	public DataSource jndiDataSource(@Value("${jndi.name}") String jndiName) {
		JndiDataSourceLookup lookup = new JndiDataSourceLookup();
		return lookup.getDataSource(jndiName);
	}

	/**
	 * When using same name (e.g. dataSource) for jdbc and jndi datasources
	 * though they have different @Profile still won't work (none will be found).
	 */
	@Profile({"test-jdbc-ds", "prod-jdbc-ds"})
	@Bean
	public DataSource stageOrProdJdbcDataSource(
			@Qualifier("jdbcDsProperties") Properties jdbcDsProperties) {
		return new HikariDataSource(new HikariConfig(jdbcDsProperties));
	}

	@Profile("in-memory-db")
	@Bean
	public DataSource inMemoryJdbcDataSource(
			@Qualifier("jdbcDsProperties") Properties jdbcDsProperties) {
		return new DriverManagerDataSource(jdbcDsProperties.getProperty("url"), jdbcDsProperties);
	}

	@Profile("in-memory-db")
	@Bean("jdbcDsProperties")
	public PropertiesFactoryBean inMemoryDsProperties() {
		return new PropertiesFactoryBeanEx("jdbc-in-memory.properties");
	}

	@Profile("test-jdbc-ds")
	@Bean("jdbcDsProperties")
	public PropertiesFactoryBean stageDsProperties() {
		return new PropertiesFactoryBeanEx("jdbc-stage.properties");
	}

	@Profile("prod-jdbc-ds")
	@Bean("jdbcDsProperties")
	public PropertiesFactoryBean prodDsProperties() {
		return new PropertiesFactoryBeanEx("jdbc-production.properties");
	}
}
