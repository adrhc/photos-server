package image.photostests.junit4.testconfig;

import image.photos.PhotosConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PhotosConfig.class,
		AsyncConfig.class, ReplacerConfig.class})
public class PhotosTestConfig {
}
