package image.exifweb.util.frameworks.hibernate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class HibernateAwareObjectMapper extends ObjectMapper {
	public HibernateAwareObjectMapper() {
		setDateFormat(new SimpleDateFormat("dd.MM.yyyy"));
		Hibernate4Module hm = new Hibernate4Module();
		hm.disable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
		hm.disable(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION);
		registerModule(hm);
	}
}
