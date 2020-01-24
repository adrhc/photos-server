package image.jpa2x;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Import({DataSourcePropertiesConfig.class})
@PropertySource("classpath:/jndi-datasource.properties")
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
		return new JndiDataSourceLookup().getDataSource(jndiName);
	}

	/**
	 * When using same name (e.g. dataSource) for jdbc and jndi data sources
	 * though they have different @Profile still won't work (none will be found).
	 */
	@Profile({"test-jdbc-ds", "prod-jdbc-ds"})
	@Bean
	public DataSource stageOrProdJdbcDataSource(
			@Qualifier("jdbcProperties") Properties jdbcProperties) {
		return new HikariDataSource(new HikariConfig(jdbcProperties));
	}

	@Profile("in-memory-db")
	@Bean
	public DataSource inMemoryJdbcDataSource(
			@Qualifier("jdbcProperties") Properties jdbcProperties) {
		return new DriverManagerDataSource(jdbcProperties.getProperty("url"), jdbcProperties);
	}
}
