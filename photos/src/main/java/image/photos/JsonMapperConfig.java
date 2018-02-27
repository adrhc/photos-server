package image.photos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * Created by adr on 2/21/18.
 */
@Configuration
public class JsonMapperConfig {
	/**
	 * jackson uses GMT by default (com.fasterxml.jackson.databind.cfg.BaseSettings._timeZone)
	 */
	public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();

		// using server's timezone
//		mapper.setDateFormat(new SimpleDateFormat("dd.MM.yyyy"));
		// using server's timezone
		mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
		// using server's GMT
//		mapper.setDateFormat(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss") {{
//			setTimeZone(TimeZone.getTimeZone("GMT"));
//		}});

		Hibernate5Module hm = new Hibernate5Module();
		hm.disable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
		hm.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
		mapper.registerModule(hm);

		return mapper;
	}
}
