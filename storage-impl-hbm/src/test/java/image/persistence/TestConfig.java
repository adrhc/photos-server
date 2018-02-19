package image.persistence;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.sql.DataSource;

/**
 * Using TestPropertySource
 * <p>
 * Created by adr on 2/19/18.
 */
@Configuration
@PropertySource("/test.properties")
@Import(HibernateConfig.class)
@Profile("test")
public class TestConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public DataSource dataSource(@Value("${jdbc.url}") String jdbcUrl,
	                             @Value("${jdbc.userName}") String userName,
	                             @Value("${jdbc.password}") String password,
	                             @Value("${jdbc.minimumIdle}") int minimumIdle,
	                             @Value("${jdbc.maximumPoolSize}") int maximumPoolSize) {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(jdbcUrl);
		ds.setUsername(userName);
		ds.setPassword(password);
		ds.setAutoCommit(false);
		ds.setMinimumIdle(minimumIdle);
		ds.setMaximumPoolSize(maximumPoolSize);
		return ds;
	}
}
