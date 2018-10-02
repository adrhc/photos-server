package image.exifweb;

import image.persistence.HibernateConfig;
import image.photos.PhotosConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by adr on 2/16/18.
 * <p>
 * PropertySource: "classpath:" is mandatory!
 */
@Configuration
@PropertySource("classpath:/exifweb.properties")
@Import({HibernateConfig.class, PhotosConfig.class,
        SubtitlesConfig.class, WebSecurityConfig.class})
public class RootConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer
    propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
