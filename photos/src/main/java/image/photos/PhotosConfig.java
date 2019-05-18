package image.photos;

import image.jpa2x.Jpa2xConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.number.NumberFormatAnnotationFormatterFactory;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

import java.util.Set;

/**
 * Created by adr on 2/19/18.
 * <p>
 * PropertySource: "classpath:" is mandatory!
 */
@Configuration
@ComponentScan
@Import({Jpa2xConfig.class})
@PropertySource("classpath:/photos.properties")
public class PhotosConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	/**
	 * see @EnableWebMvc
	 * see @Bean public FormattingConversionService WebMvcConfigurationSupport.mvcConversionService()
	 * Order matters for @Bean({"mvcConversionService", "conversionService"}).
	 * <p>
	 * ERROR when using conversionService() with ConversionServiceFactoryBean:
	 * "expected single matching bean but found 2: mvcConversionService,conversionService"
	 *
	 * @param converterSet
	 * @param converterFactories
	 * @return
	 */
	@Autowired
	@Bean({"mvcConversionService", "conversionService"})
	public FormattingConversionService mvcConversionService(
			Set<Converter> converterSet,
			Set<ConverterFactory> converterFactories) {
		DefaultFormattingConversionService conversionService =
				new DefaultFormattingConversionService(false);

		// custom converters & converterFactories
		converterSet.forEach(conversionService::addConverter);
		converterFactories.forEach(conversionService::addConverterFactory);

		// Ensure @NumberFormat is still supported
		conversionService.addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());

		// Register date conversion with a specific global format
		DateFormatterRegistrar registrar = new DateFormatterRegistrar();
		registrar.setFormatter(new DateFormatter("dd.MM.yyyy"));
		registrar.registerFormatters(conversionService);

		return conversionService;
	}
}
