package image.photos;

import image.infrastructure.messaging.MessagingConfig;
import image.jpa2x.Jpa2xConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by adr on 2/19/18.
 * <p>
 * PropertySource: "classpath:" is mandatory!
 */
@Configuration
@ComponentScan
@Import({MessagingConfig.class, Jpa2xConfig.class})
@PropertySource("classpath:photos.properties")
public class PhotosConfig {}
