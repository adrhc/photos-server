package image.persistence.repository.junit5.inmemorydb;

import image.persistence.entity.Album;
import image.persistence.repository.CacheStatisticsRepository;
import image.persistence.repository.junit5.springconfig.Junit5HbmInMemoryDbConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@NotThreadSafe
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
