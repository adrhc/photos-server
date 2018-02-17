package image.exifweb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.text.SimpleDateFormat;

/**
 * Created by adr on 2/16/18.
 */
@Configuration
@Import(HibernateConfig.class)
@ComponentScan(basePackageClasses = RootConfig.class,
		basePackages = "subtitles",
		excludeFilters = {@ComponentScan.Filter(Controller.class),
				@ComponentScan.Filter(ControllerAdvice.class),
				@ComponentScan.Filter(Configuration.class)})
//				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
//						classes = subtitles.App.class)})
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
