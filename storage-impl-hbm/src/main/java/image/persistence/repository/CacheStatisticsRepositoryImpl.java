package image.persistence.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by adr on 2/18/18.
 */
@Component
public class CacheStatisticsRepositoryImpl implements CacheStatisticsRepository {
	@Inject
	private SessionFactory sessionFactory;

	@Override
	public Map getSecondLevelCacheStatistics(String name) {
		return this.sessionFactory.getStatistics()
				.getSecondLevelCacheStatistics(name).getEntries();
	}
}
