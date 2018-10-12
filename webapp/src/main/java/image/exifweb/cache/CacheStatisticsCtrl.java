package image.exifweb.cache;

import image.persistence.entity.Album;
import image.persistence.repository.CacheStatisticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by adr on 2/2/18.
 */
@RestController
@RequestMapping("/cacheStat")
public class CacheStatisticsCtrl {
	private static final Logger logger = LoggerFactory.getLogger(CacheStatisticsCtrl.class);

	@Inject
	private CacheStatisticsRepository statisticsRepository;

	@RequestMapping(method = RequestMethod.GET)
	public void printCacheStatistics() {
		Map cacheEntries = this.statisticsRepository
				.getSecondLevelCacheStatistics(Album.class.getName());
		logger.debug("END {}", cacheEntries);
	}
}
