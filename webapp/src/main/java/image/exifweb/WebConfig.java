package image.exifweb;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adr on 2/15/18.
 * <p>
 * Scanner is including only the listed classes.
 */
@Configuration
@ComponentScan(useDefaultFilters = false,
		includeFilters = @ComponentScan.Filter(
				{Controller.class, ControllerAdvice.class}))
@Import(WebContextUtilities.class)
@EnableWebMvc
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class WebConfig implements WebMvcConfigurer {
	@Autowired
	private ThreadPoolTaskExecutor asyncExecutor;
	@Autowired
	private ObjectMapper objectMapper;
	@Value("${async.timeout}")
	private long asyncTimeout;

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setDefaultTimeout(this.asyncTimeout);
		configurer.setTaskExecutor(this.asyncExecutor);
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.stream().filter(c -> c instanceof MappingJackson2HttpMessageConverter).forEach(c -> {
			// Check default included objectMapper._registeredModuleTypes,
			// e.g. Jdk8Module, JavaTimeModule when creating the ObjectMapper
			// without using Jackson2ObjectMapperBuilder!
			//
			// see image.photos.JsonMapperConfig
			((MappingJackson2HttpMessageConverter) c).setObjectMapper(this.objectMapper);
		});
	}

	/**
	 * see https://docs.spring.io/spring/docs/5.2.0.M1/spring-framework-reference/web.html#mvc-config-view-resolvers
	 * <p>
	 * http://127.0.0.1:8080/exifweb/index.jsp
	 */
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.enableContentNegotiation(mappingJackson2JsonView());
		registry.jsp("/", ".jsp").cache(false);
	}

	/**
	 * http://127.0.0.1:8080/exifweb/app/
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
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

	/**
	 * MappingJackson2JsonView: Spring MVC View that renders JSON content by
	 * serializing the model for the current request using Jackson 2's ObjectMapper.
	 */
	@Bean
	public MappingJackson2JsonView mappingJackson2JsonView() {
		return new MappingJackson2JsonView(this.objectMapper);
	}
}
