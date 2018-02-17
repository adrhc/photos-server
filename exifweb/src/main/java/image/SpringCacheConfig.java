package image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * Created by adr on 2/17/18.
 */
@Configuration
@EnableCaching(mode = AdviceMode.ASPECTJ)
public class SpringCacheConfig {
	/**
	 * http://www.baeldung.com/spring-cache-tutorial
	 * <p>
	 * <bean p:name="covers" class="org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean"/>
	 */
	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager("covers");
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("dd.MM.yyyy"));
		Hibernate4Module hm = new Hibernate4Module();
		hm.disable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
		hm.disable(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION);
		objectMapper.registerModule(hm);
		return objectMapper;
	}
}
