package image.hbm.repository;

import image.persistence.repository.CacheStatisticsRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by adr on 2/18/18.
 */
@Component
public class CacheStatisticsRepositoryImpl implements CacheStatisticsRepository {
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Map getSecondLevelCacheStatistics(String name) {
		return this.sessionFactory.getStatistics()
				.getSecondLevelCacheStatistics(name).getEntries();
	}
}
