package image.exifweb;

import image.exifweb.web.security.WebSecurityComponent;
import image.photos.PhotosConfig;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import subtitles.SubtitlesConfig;

/**
 * Created by adr on 2/16/18.
 * <p>
 * PropertySource: "classpath:" is mandatory!
 */
@Configuration
@ComponentScan(useDefaultFilters = false,
		excludeFilters = @ComponentScan.Filter({Configuration.class,
				Controller.class, RestController.class,
				ControllerAdvice.class, WebSecurityComponent.class}),
		includeFilters = @ComponentScan.Filter({Component.class, Service.class}))
@PropertySource("classpath:/exifweb.properties")
@Import({SubtitlesConfig.class, PhotosConfig.class})
public class RootConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
