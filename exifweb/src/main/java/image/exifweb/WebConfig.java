package image.exifweb;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by adr on 2/15/18.
 */
@Configuration
@Import({AsyncAndSchedulingConfig.class,
		SpringCacheConfig.class})
//		WebSecurityConfig.class})
@EnableWebMvc
@ComponentScan(basePackageClasses = WebConfig.class, useDefaultFilters = false,
		includeFilters = {@ComponentScan.Filter(Controller.class),
				@ComponentScan.Filter(ControllerAdvice.class)})
public class WebConfig extends WebMvcConfigurerAdapter {
	@Inject
	private ObjectMapper objectMapper;

//	@Bean
//	public static PropertySourcesPlaceholderConfigurer
//	propertySourcesPlaceholderConfigurer() {
//		PropertySourcesPlaceholderConfigurer p =
//				new PropertySourcesPlaceholderConfigurer();
//		p.setLocations(new ClassPathResource("exifweb.properties"),
//				new ClassPathResource("classpath*:exifweb-overwrite.properties"));
//		p.setIgnoreResourceNotFound(true);
//		p.setIgnoreUnresolvablePlaceholders(true);
//		return p;
//	}

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

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setDefaultTimeout(7200000);
		super.configureAsyncSupport(configurer);
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
	}

	@Bean
	public ViewResolver contentNegotiatingViewResolver(
			ContentNegotiationManager manager) {
		ContentNegotiatingViewResolver resolver =
				new ContentNegotiatingViewResolver();
		resolver.setContentNegotiationManager(manager);
		List<ViewResolver> viewResolvers = new ArrayList<>();
		InternalResourceViewResolver r2 = new InternalResourceViewResolver();
		r2.setPrefix("/app/");
		r2.setSuffix(".jsp");
		r2.setCache(false);
		viewResolvers.add(r2);
		resolver.setViewResolvers(viewResolvers);
		resolver.setDefaultViews(Collections.singletonList(jacksonConverter()));
		return resolver;
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
		return new MappingJackson2JsonView(objectMapper);
	}
}
