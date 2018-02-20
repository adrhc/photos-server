package image.photos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.text.SimpleDateFormat;

/**
 * Created by adr on 2/19/18.
 */
@Configuration
@PropertySource("classpath:/photos.properties")
@ComponentScan(basePackageClasses = PhotosConfig.class,
		excludeFilters = @ComponentScan.Filter(Configuration.class))
public class PhotosConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
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
