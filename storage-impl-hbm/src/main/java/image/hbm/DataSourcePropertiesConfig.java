package image.hbm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class DataSourcePropertiesConfig {
	@Profile("in-memory-db")
	@Bean("jdbcProperties")
	public Properties inMemoryDsProperties() throws IOException {
		return PropertiesLoaderUtils.loadAllProperties("jdbc-datasource/jdbc-in-memory.properties");
	}

	@Profile("test-jdbc-ds")
	@Bean("jdbcProperties")
	public Properties stageDsProperties() throws IOException {
		return PropertiesLoaderUtils.loadAllProperties("jdbc-datasource/jdbc-stage.properties");
	}

	@Profile("prod-jdbc-ds")
	@Bean("jdbcProperties")
	public Properties prodDsProperties() throws IOException {
		return PropertiesLoaderUtils.loadAllProperties("jdbc-datasource/jdbc-production.properties");
	}
}
