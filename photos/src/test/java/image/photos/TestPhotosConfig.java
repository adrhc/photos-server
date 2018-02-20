package image.photos;

import image.persistence.HibernateConfig;
import org.springframework.context.annotation.*;

/**
 * Using TestPropertySource
 * <p>
 * Created by adr on 2/19/18.
 */
@Configuration
@PropertySource("classpath:/jdbc-datasource.properties")
@Import({HibernateConfig.class, PhotosConfig.class})
@Profile("integration-tests")
public class TestPhotosConfig {
}
