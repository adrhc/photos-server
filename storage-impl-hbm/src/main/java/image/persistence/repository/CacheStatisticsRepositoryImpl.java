package image.persistence.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
	@Transactional(readOnly = true)
	public Map getSecondLevelCacheStatistics(String name) {
		return sessionFactory.getStatistics()
				.getSecondLevelCacheStatistics(name)
				.getEntries();
	}
}