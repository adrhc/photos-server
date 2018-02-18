package image.exifweb.cache;

import image.persistence.entity.Album;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Map;

/**
 * Created by adr on 2/2/18.
 */
@RestController("/cacheStat")
public class CacheStatisticsCtrl {
	private static final Logger logger = LoggerFactory.getLogger(CacheStatisticsCtrl.class);

	@Inject
	private SessionFactory sessionFactory;

	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public void printCacheStatistics() {
		Map cacheEntries = sessionFactory.getStatistics()
				.getSecondLevelCacheStatistics(Album.class.getName())
				.getEntries();
		logger.debug("END {}", cacheEntries);
	}
}
