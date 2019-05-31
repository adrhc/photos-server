package image.exifweb;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class RootContextUtilities {
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
