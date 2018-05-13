package image.persistence;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

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
	@Profile("prod-jdbc-ds")
	@Bean
	public DataSource prodJdbcDataSource(@Value("${prod.jdbc.url}") String jdbcUrl,
	                                     @Value("${prod.jdbc.userName}") String userName,
	                                     @Value("${prod.jdbc.password}") String password,
	                                     @Value("${prod.jdbc.minimumIdle}") int minimumIdle,
	                                     @Value("${prod.jdbc.maximumPoolSize}") int maximumPoolSize) {
		return hikariDataSourceOf(jdbcUrl, userName, password, minimumIdle, maximumPoolSize);
	}

	@Profile("test-jdbc-ds")
	@Bean
	public DataSource testJdbcDataSource(@Value("${test.jdbc.url}") String jdbcUrl,
	                                     @Value("${test.jdbc.userName}") String userName,
	                                     @Value("${test.jdbc.password}") String password,
	                                     @Value("${test.jdbc.minimumIdle}") int minimumIdle,
	                                     @Value("${test.jdbc.maximumPoolSize}") int maximumPoolSize) {
		return hikariDataSourceOf(jdbcUrl, userName, password, minimumIdle, maximumPoolSize);
	}

	private HikariDataSource hikariDataSourceOf(String jdbcUrl, String userName,
	                                            String password, int minimumIdle,
	                                            int maximumPoolSize) {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(jdbcUrl);
		ds.setUsername(userName);
		ds.setPassword(password);
		ds.setAutoCommit(false);
		ds.setMinimumIdle(minimumIdle);
		ds.setMaximumPoolSize(maximumPoolSize);
		return ds;
	}

	@Profile("in-memory-db")
	@Bean
	public DataSource inMemoryDataSource(@Value("${ramdb.jdbc.driverClass}") String driverClass,
	                                     @Value("${ramdb.jdbc.url}") String jdbcUrl,
	                                     @Value("${ramdb.jdbc.userName}") String userName,
	                                     @Value("${ramdb.jdbc.password}") String password) {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName(driverClass);
		ds.setUrl(jdbcUrl);
		if (StringUtils.hasText(userName)) {
			ds.setUsername(userName);
			if (StringUtils.hasText(password)) {
				ds.setPassword(password);
			}
		}
		return ds;
	}
}
