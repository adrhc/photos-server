package image.exifweb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import image.exifweb.web.security.WebSecurityComponent;
import image.persistence.HibernateConfig;
import image.photos.PhotosConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;

/**
 * Created by adr on 2/16/18.
 */
@Configuration
@Import({HibernateConfig.class, PhotosConfig.class, WebSecurityConfig.class})
@ComponentScan(basePackageClasses = RootConfig.class,
		basePackages = "subtitles",
		excludeFilters = {@ComponentScan.Filter(Controller.class),
				@ComponentScan.Filter(RestController.class),
				@ComponentScan.Filter(ControllerAdvice.class),
				@ComponentScan.Filter(WebSecurityComponent.class),
				@ComponentScan.Filter(Configuration.class)})
public class RootConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer p =
				new PropertySourcesPlaceholderConfigurer();
		p.setLocations(new ClassPathResource("/exifweb.properties"),
				new ClassPathResource("/subs-extract-app-config.properties"),
				new ClassPathResource("/exifweb-overwrite.properties"));
		p.setIgnoreResourceNotFound(true);
		return p;
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("dd.MM.yyyy"));
		Hibernate4Module hm = new Hibernate4Module();
		hm.disable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
		hm.disable(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION);
		objectMapper.registerModule(hm);
		return objectMapper;
	}
}
