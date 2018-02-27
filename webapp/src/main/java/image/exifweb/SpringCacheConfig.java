package image.exifweb;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

/**
 * see tech.log
 * <p>
 * Created by adr on 2/17/18.
 */
@Configuration
@EnableCaching(mode = AdviceMode.ASPECTJ)
public class SpringCacheConfig {
	/**
	 * http://www.baeldung.com/spring-cache-tutorial
	 * <p>
	 * <bean p:name="covers" class="org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean"/>
	 * <p>
	 * conflicts with hibernate second level cache
	 */
	@Bean
	public CacheManager cacheManager(Collection<Cache> caches) {
		SimpleCacheManager scm = new SimpleCacheManager();
		scm.setCaches(caches);
		return scm;
	}

	@Bean
	public ConcurrentMapCacheFactoryBean coversCache() {
		ConcurrentMapCacheFactoryBean covers =
				new ConcurrentMapCacheFactoryBean();
		covers.setName("covers");
		return covers;
	}
}
