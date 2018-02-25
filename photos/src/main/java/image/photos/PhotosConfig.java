package image.photos;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by adr on 2/19/18.
 */
@Configuration
@PropertySource("classpath:/photos.properties")
@Import(JsonMapperConfig.class)
@ComponentScan(basePackageClasses = PhotosConfig.class,
		excludeFilters = @ComponentScan.Filter(Configuration.class))
public class PhotosConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
