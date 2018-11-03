package image.jpa2x.services;

import image.persistence.entity.Album;
import image.persistence.repository.CacheStatisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;

@Slf4j
@Component
public class CacheStatisticsRepositoryImpl implements CacheStatisticsRepository {
	@PersistenceContext
	private EntityManager em;

	@Override
	public Map getSecondLevelCacheStatistics(String name) {
		Cache cache = this.em.getEntityManagerFactory().getCache();
		log.debug("Album with id = 1 exists: {}", cache.contains(Album.class, 1));
		// ((SelectableConcurrentHashMap)((MemoryStore)((Cache)((EhcacheEntityRegion)((ReadWriteEhcacheEntityRegionAccessStrategy)((NonstopAwareEntityRegionAccessStrategy)((java.util.concurrent.ConcurrentHashMap.MapEntry)((java.util.concurrent.ConcurrentHashMap)((CacheImpl)cache).entityRegionAccessStrategyMap).entrySet().toArray()[0]).getValue()).actualStrategy).region).cache).compoundStore).map).segments
/*
		CacheImpl cacheImpl = this.em.getEntityManagerFactory().getCache().unwrap(CacheImpl.class);
		log.debug("secondLevelCacheRegionNames:\n{}", Arrays.stream(
				cacheImpl.getSecondLevelCacheRegionNames()).collect(Collectors.joining("\n")));
		return cacheImpl.getSessionFactory().getStatistics()
				.getSecondLevelCacheStatistics(name).getEntries();
*/
		return null;
	}
}
