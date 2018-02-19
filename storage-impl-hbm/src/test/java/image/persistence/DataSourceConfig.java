package image.persistence;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Created by adr on 2/19/18.
 */
@Configuration
@Profile("test")
public class DataSourceConfig {
	@Bean
	public DataSource dataSource() {
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/exifweb");
		ds.setUsername("exifweb");
		ds.setPassword("exifweb");
		ds.setAutoCommit(false);
		ds.setMinimumIdle(1);
		ds.setMaximumPoolSize(3);
		return ds;
	}
}
