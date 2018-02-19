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
	@Value("${jdbc.url}")
	private String jdbcUrl;
	@Value("${jdbc.userName}")
	private String userName;
	@Value("${jdbc.password}")
	private String password;
	@Value("${jdbc.minimumIdle}")
	private int minimumIdle;
	@Value("${jdbc.maximumPoolSize}")
	private int maximumPoolSize;

	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public DataSource dataSource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(jdbcUrl);
		ds.setUsername(userName);
		ds.setPassword(password);
		ds.setAutoCommit(false);
		ds.setMinimumIdle(1);
		ds.setMaximumPoolSize(2);
		return ds;
	}
}
