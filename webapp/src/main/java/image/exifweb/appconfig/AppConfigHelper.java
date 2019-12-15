package image.exifweb.appconfig;

import image.persistence.entity.AppConfig;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by adr on 1/31/18.
 */
public abstract class AppConfigHelper {
	private static Logger logger = LoggerFactory.getLogger(AppConfigHelper.class);

	public static void evictAppConfigCache() {
		logger.debug("removing \"AppConfig\" cache");
		CacheManager.ALL_CACHE_MANAGERS.forEach(c -> c.getCache(AppConfig.class.getName()));
	}
}
