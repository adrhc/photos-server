package image.exifweb.appconfig;

import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by adr on 1/31/18.
 */
public interface IAppConfigCache {
	Logger logger = LoggerFactory.getLogger(IAppConfigCache.class);

	default void evictAppConfigCache() {
		logger.debug("removing \"AppConfig\" cache");
		CacheManager.ALL_CACHE_MANAGERS.get(0).getCache("AppConfig");
	}
}
