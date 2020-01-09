package image.jpa2x.util;

import org.hibernate.cache.spi.CacheImplementor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class Jpa2ndLevelCacheUtils {
	@PersistenceContext
	protected EntityManager em;

	public void evictQueryRegions() {
		CacheImplementor cache = this.em.getEntityManagerFactory().getCache().unwrap(CacheImplementor.class);
		cache.evictQueryRegions();
	}

	public void evictAll() {
		this.em.getEntityManagerFactory().getCache().evictAll();
	}
}
