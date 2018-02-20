package image.persistence.integration.repository;

import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;
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

	public Integer getConfigInt(AppConfigEnum ace) {
		String s = getConfig(ace);
		if (s == null) {
			return 0;
		}
		return Integer.parseInt(s);
	}

	public String getConfig(AppConfigEnum appConfigEnum) {
		return getAppConfigById(appConfigEnum.getValue()).getValue();
	}

	public int getPhotosPerPage() {
		return getConfigInt(AppConfigEnum.PHOTOS_PER_PAGE);
	}

	public String getLinuxAlbumPath() {
		return getConfig(AppConfigEnum.LINUX_ALBUMS_PATH);
	}

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

	@Transactional(readOnly = true)
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
