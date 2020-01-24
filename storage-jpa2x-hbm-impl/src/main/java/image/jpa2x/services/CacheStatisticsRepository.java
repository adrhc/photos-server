package image.jpa2x.services;

import java.util.Map;

/**
 * Created by adrianpetre on 23.02.2018.
 */
public interface CacheStatisticsRepository {
	Map getSecondLevelCacheStatistics(String name);
}
