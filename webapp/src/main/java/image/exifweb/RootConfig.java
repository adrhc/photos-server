package image.exifweb;

import image.exifweb.web.security.WebSecurityComponent;
import image.photos.PhotosConfig;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subtitles.SubtitlesConfig;

/**
 * Created by adr on 2/16/18.
 * <p>
 * PropertySource: "classpath:" is mandatory!
 */
@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter({Configuration.class,
		Controller.class, ControllerAdvice.class, RestControllerAdvice.class, WebSecurityComponent.class}))
@PropertySource("classpath:/exifweb.properties")
@Import({SubtitlesConfig.class, PhotosConfig.class})
public class RootConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	/**
	 * somehow when not using "messageSource" then
	 * RequestExceptionHandler can't find this bean
	 *
	 * @return
	 */
	@Bean(name = {"msg", "messages", "messageSource"})
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource ms =
				new ReloadableResourceBundleMessageSource();
		ms.setBasenames("classpath:text/messages",
				"classpath:org/hibernate/validator/ValidationMessages");
		ms.setDefaultEncoding("UTF-8");
		ms.setFallbackToSystemLocale(true);
		return ms;
	}
}
