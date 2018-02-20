package image.persistence.integration.repository;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by adr on 2/18/18.
 */
@Component
public class CacheStatisticsRepository {
	@Inject
	private SessionFactory sessionFactory;

	@Transactional(readOnly = true)
	public Map getSecondLevelCacheStatistics(String name) {
		return sessionFactory.getStatistics()
				.getSecondLevelCacheStatistics(name)
				.getEntries();
	}
}
