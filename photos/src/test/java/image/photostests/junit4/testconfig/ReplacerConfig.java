package image.photostests.junit4.testconfig;

import image.photos.infrastructure.filestore.FileStoreService;
import image.photostests.overrides.infrastructure.filestore.FileStoreServiceTestImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReplacerConfig {
	@Bean
	public FileStoreService fileStoreService() {
		return new FileStoreServiceTestImpl();
	}
}
