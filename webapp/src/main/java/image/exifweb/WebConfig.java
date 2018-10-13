package image.exifweb;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.exifweb.web.security.WebSecurityComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adr on 2/15/18.
 */
@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter({Configuration.class,
		Component.class, Service.class, WebSecurityComponent.class}))
@EnableWebMvc
// use proxyTargetClass = true when not having interfaces for @Controller classes
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import({AsyncAndSchedulingConfig.class, SpringCacheConfig.class})
public class WebConfig implements WebMvcConfigurer {
	@Inject
	private ObjectMapper objectMapper;
	@Value("${async.timeout}")
	private long asyncTimeout;

	/**
	 * Using RootConfig:exifweb.properties.
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setDefaultTimeout(this.asyncTimeout);
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new MappingJackson2HttpMessageConverter(this.objectMapper));
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.jsp("/app/", ".jsp").cache(false);
		registry.enableContentNegotiation(jacksonConverter());
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON_UTF8);
		Map<String, MediaType> mediaTypes = new HashMap<>();
		mediaTypes.put("json", MediaType.APPLICATION_JSON_UTF8);
		mediaTypes.put("atom", MediaType.APPLICATION_ATOM_XML);
		mediaTypes.put("html", MediaType.TEXT_HTML);
		mediaTypes.put("xml", MediaType.TEXT_XML);
		mediaTypes.put("kml", MediaType.valueOf("application/vnd.google-earth.kml"));
		configurer.mediaTypes(mediaTypes);
	}

	@Bean
	public MappingJackson2JsonView jacksonConverter() {
		return new MappingJackson2JsonView(this.objectMapper);
	}
}
