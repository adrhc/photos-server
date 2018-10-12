package image.exifweb;

import image.exifweb.web.security.WebSecurityComponent;
import image.photos.PhotosConfig;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by adr on 2/16/18.
 * <p>
 * PropertySource: "classpath:" is mandatory!
 */
@Configuration
@ComponentScan(basePackageClasses = RootConfig.class,
		basePackages = "subtitles",
		excludeFilters = {@ComponentScan.Filter(Controller.class),
				@ComponentScan.Filter(RestController.class),
				@ComponentScan.Filter(ControllerAdvice.class),
				@ComponentScan.Filter(WebSecurityComponent.class),
				@ComponentScan.Filter(Configuration.class)})
@PropertySource("classpath:/exifweb.properties")
@Import({PhotosConfig.class, SubtitlesConfig.class, WebSecurityConfig.class})
public class RootConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
