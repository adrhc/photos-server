package image.exifweb;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
