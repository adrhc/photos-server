package image.exifweb.system.persistence.repositories;

import image.persistence.entity.AppConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * Created by adr on 2/10/18.
 */
@Service
public class AppConfigRepository {
	@Inject
	private SessionFactory sessionFactory;

	@Transactional
	public AppConfig getAppConfigById(Integer id) {
		return (AppConfig) sessionFactory.getCurrentSession().get(AppConfig.class, id);
	}

	@Transactional
	public AppConfig getAppConfigByName(String name) {
		return (AppConfig) sessionFactory.getCurrentSession().createCriteria(AppConfig.class)
				.setCacheable(true).add(Restrictions.eq("name", name)).uniqueResult();
	}

	@Transactional
	public void update(List<AppConfig> appConfigs) {
		List<AppConfig> dbAppConfigs = getAppConfigs();
		for (AppConfig dbAppConfig : dbAppConfigs) {
			for (AppConfig appConfig : appConfigs) {
				if (dbAppConfig.getId().equals(appConfig.getId())) {
					dbAppConfig.setValue(appConfig.getValue());
					break;
				}
			}
		}
	}

	@Transactional
	public List<AppConfig> getAppConfigs() {
		Session session = sessionFactory.getCurrentSession();
		return (List<AppConfig>) session.createCriteria(AppConfig.class).setCacheable(true).list();
	}

	@Transactional(readOnly = true)
	public List<AppConfig> testGetNoCacheableOrderedAppConfigs() {
		Session session = sessionFactory.getCurrentSession();
		return (List<AppConfig>) session.createCriteria(AppConfig.class)
				.addOrder(Order.asc("name")).list();
	}

	@Transactional(readOnly = true)
	public AppConfig testGetNoCacheableAppConfigByName(String name) {
		Session session = sessionFactory.getCurrentSession();
		return (AppConfig) session.createCriteria(AppConfig.class)
				.add(Restrictions.eq("name", name)).uniqueResult();
	}

	@Transactional(readOnly = true)
	public Date getDBNow() {
		Session session = sessionFactory.getCurrentSession();
		return (Date) session.createSQLQuery("SELECT now() FROM dual").uniqueResult();
	}
}
