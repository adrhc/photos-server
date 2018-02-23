package image.photos;

import image.persistence.HibernateConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Using TestPropertySource
 * <p>
 * Created by adr on 2/19/18.
 */
@Configuration
@Import({HibernateConfig.class, PhotosConfig.class})
public class TestPhotosConfig {
}
