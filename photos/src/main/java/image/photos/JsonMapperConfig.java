package image.photos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by adr on 2/21/18.
 */
@Configuration
public class JsonMapperConfig {
	/**
	 * jackson uses GMT by default (com.fasterxml.jackson.databind.cfg.BaseSettings._timeZone)
	 */
	public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";

	/**
	 * see image.exifweb.WebConfig
	 */
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();

		// using server's timezone
//		mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
		// GMT = UTC
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		mapper.setDateFormat(simpleDateFormat);

		Hibernate5Module hm = new Hibernate5Module()
				.disable(Hibernate5Module.Feature.FORCE_LAZY_LOADING)
				.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
		mapper.registerModule(hm);

		// see spring boot: ParameterNamesModuleConfiguration
		mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.DEFAULT));

		// check default included objectMapper._registeredModuleTypes,
		// e.g. Jdk8Module, JavaTimeModule when creating the ObjectMapper
		// without using Jackson2ObjectMapperBuilder
		mapper.registerModule(new Jdk8Module());
		mapper.registerModule(new JavaTimeModule());

		return mapper;
	}
}
