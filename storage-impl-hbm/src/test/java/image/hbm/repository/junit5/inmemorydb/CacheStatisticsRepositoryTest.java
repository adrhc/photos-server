package image.hbm.repository.junit5.inmemorydb;

import image.hbm.repository.junit5.springconfig.Junit5HbmInMemoryDbConfig;
import image.persistence.entity.Album;
import image.persistence.repository.CacheStatisticsRepository;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Junit5HbmInMemoryDbConfig
class CacheStatisticsRepositoryTest {
	@Inject
	private CacheStatisticsRepository cacheStatisticsRepository;

	@Test
	void getSecondLevelCacheStatistics() {
		Map cacheEntries =
				this.cacheStatisticsRepository.getSecondLevelCacheStatistics(Album.class.getName());
		assertNotNull(cacheEntries);
	}
}
