package image.jpa2x;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Profile("jpa2x-hbm-impl")
@Configuration
public class Jpa2xContextUtilities {
	/**
	 * LocalContainerEntityManagerFactoryBean implements PersistenceExceptionTranslator
	 * https://stackoverflow.com/questions/41801512/spring-how-exception-translation-works
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
