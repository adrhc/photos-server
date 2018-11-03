package image.hbm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

/**
 * Replaced by hibernate-*.properties configuration (see tech.log).
 * <p>
 * Created by adr on 2/25/18.
 */
//@Configuration
public class HibernateCacheConfig {
	@Autowired
	private Environment env;

	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactory() {
		EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
		cacheManagerFactoryBean.setConfigLocation(new ClassPathResource(
				this.env.getProperty("net.sf.ehcache.configurationResourceName")));
//		cacheManagerFactoryBean.setCacheManagerName(env.get("ehcache.hibernate.name"));
		cacheManagerFactoryBean.setShared(true);
//		cacheManagerFactoryBean.setAcceptExisting(true);
		return cacheManagerFactoryBean;
	}

	@Bean
	public CacheManager cacheManager() {
		EhCacheCacheManager cacheManager = new EhCacheCacheManager();
		cacheManager.setCacheManager(ehCacheManagerFactory().getObject());
		cacheManager.setTransactionAware(true);
		return cacheManager;
	}
}
