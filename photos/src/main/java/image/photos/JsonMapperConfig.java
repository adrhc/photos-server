package image.photos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
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
		Hibernate4Module hm = new Hibernate4Module();
		hm.disable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
		hm.disable(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION);
		mapper.registerModule(hm);
		return mapper;
	}
}
