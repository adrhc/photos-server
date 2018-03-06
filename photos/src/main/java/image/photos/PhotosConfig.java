package image.photos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by adr on 2/19/18.
 */
@Configuration
@PropertySource("classpath:/photos.properties")
@Import(JsonMapperConfig.class)
@ComponentScan(basePackageClasses = PhotosConfig.class,
		excludeFilters = @ComponentScan.Filter(Configuration.class))
public class PhotosConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer
	propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Autowired
	@Bean
	public ConversionService conversionService(Set<Converter> converterSet,
	                                           Set<ConverterFactory> converterFactories) {
		ConversionServiceFactoryBean factoryBean = new ConversionServiceFactoryBean();
		Set converters = new HashSet();
		converters.addAll(converterSet);
		converters.addAll(converterFactories);
		factoryBean.setConverters(converters);
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}
}
