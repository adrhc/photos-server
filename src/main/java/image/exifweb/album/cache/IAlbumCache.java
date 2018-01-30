package image.exifweb.album.cache;

import image.exifweb.persistence.Album;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

/**
 * Created by adr on 1/30/18.
 */
public interface IAlbumCache {
	Logger logger = LoggerFactory.getLogger(IAlbumCache.class);

	@Caching(evict = {
			@CacheEvict(value = "album", key = "#album.name"),
			@CacheEvict(value = "album", key = "#album.id")
	})
	default void evictAlbumCache(Album album) {
		logger.debug("removing {} album from cache", album.getName());
	}

	@CacheEvict(value = "covers", allEntries = true)
	default void evictCoversCache() {
		logger.debug("removing \"covers\" cache");
	}
}
