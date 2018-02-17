package image.exifweb;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Created by adr on 2/16/18.
 */
@Configuration
@Import(HibernateConfig.class)
@ComponentScan(basePackageClasses = RootConfig.class,
		basePackages = "subtitles",
		excludeFilters = {@ComponentScan.Filter(Controller.class),
				@ComponentScan.Filter(ControllerAdvice.class),
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
						classes = subtitles.App.class)})
public class RootConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer p =
				new PropertySourcesPlaceholderConfigurer();
		p.setLocations(new ClassPathResource("exifweb.properties"),
				new ClassPathResource("subs-extract-app-config"),
				new ClassPathResource("classpath*:exifweb-overwrite.properties"));
		p.setIgnoreResourceNotFound(true);
		p.setIgnoreUnresolvablePlaceholders(true);
		return p;
	}
}
