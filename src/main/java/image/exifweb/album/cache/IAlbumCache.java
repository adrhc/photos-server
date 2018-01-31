package image.exifweb.album.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;

/**
 * Created by adr on 1/30/18.
 */
public interface IAlbumCache {
	Logger logger = LoggerFactory.getLogger(IAlbumCache.class);

	@CacheEvict(value = "covers", allEntries = true)
	default void evictCoversCache() {
		logger.debug("removing \"covers\" cache");
	}
}
