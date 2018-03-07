package image.persistence.repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by adrianpetre on 23.02.2018.
 */
public interface CacheStatisticsRepository {
	@Transactional(readOnly = true)
	Map getSecondLevelCacheStatistics(String name);
}
