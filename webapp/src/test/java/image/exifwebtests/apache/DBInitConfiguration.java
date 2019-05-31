package image.exifwebtests.apache;

import image.exifwebtests.util.PatchedDataSourceInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DBInitConfiguration {
	@Bean
	@Autowired
	public PatchedDataSourceInitializer dataSourceInitializer(
			DataSource dataSource, PlatformTransactionManager transactionManager) {
		return new PatchedDataSourceInitializer()
				.dataSource(dataSource)
				.transactionManager(transactionManager)
				.databasePopulator(new ResourceDatabasePopulator(
//						new ClassPathResource("clean.sql"),
						new ClassPathResource("appconfig.sql"),
						new ClassPathResource("album.sql"),
						new ClassPathResource("image.sql"),
						new ClassPathResource("cover.sql")
				));
	}
}
