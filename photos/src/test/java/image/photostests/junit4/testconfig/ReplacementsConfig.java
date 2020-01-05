package image.photostests.junit4.testconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.photos.infrastructure.filestore.FileStoreService;
import image.photostests.overrides.infrastructure.filestore.FileStoreServiceTestImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReplacementsConfig {
	@Bean
	public FileStoreService fileStoreService(ObjectMapper mapper) {
		return new FileStoreServiceTestImpl(mapper);
	}
}
