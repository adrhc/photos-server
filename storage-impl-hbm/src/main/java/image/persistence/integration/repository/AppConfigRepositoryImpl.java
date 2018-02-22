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
public class AppConfigRepositoryImpl implements AppConfigRepository {
	@Inject
	private SessionFactory sessionFactory;

	private Integer getConfigInteger(AppConfigEnum ace) {
		String s = getConfig(ace);
		if (s == null) {
			return 0;
		}
		return Integer.parseInt(s);
	}

	private String getConfig(AppConfigEnum appConfigEnum) {
		return getAppConfigById(appConfigEnum.getValue()).getValue();
	}

	@Override
	public Integer getPhotosPerPage() {
		return getConfigInteger(AppConfigEnum.PHOTOS_PER_PAGE);
	}

	@Override
	public String getLinuxAlbumPath() {
		return getConfig(AppConfigEnum.LINUX_ALBUMS_PATH);
	}

	@Override
	@Transactional
	public AppConfig getAppConfigById(Integer id) {
		return (AppConfig) sessionFactory.getCurrentSession().get(AppConfig.class, id);
	}

	@Override
	@Transactional
	public AppConfig getAppConfigByName(String name) {
		return (AppConfig) sessionFactory.getCurrentSession().createCriteria(AppConfig.class)
				.setCacheable(true).add(Restrictions.eq("name", name)).uniqueResult();
	}

	@Override
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

	@Override
	@Transactional(readOnly = true)
	public List<AppConfig> getAppConfigs() {
		Session session = sessionFactory.getCurrentSession();
		return (List<AppConfig>) session.createCriteria(AppConfig.class).setCacheable(true).list();
	}

	@Override
	@Transactional(readOnly = true)
	public List<AppConfig> testGetNoCacheableOrderedAppConfigs() {
		Session session = sessionFactory.getCurrentSession();
		return (List<AppConfig>) session.createCriteria(AppConfig.class)
				.addOrder(Order.asc("name")).list();
	}

	@Override
	@Transactional(readOnly = true)
	public AppConfig testGetNoCacheableAppConfigByName(String name) {
		Session session = sessionFactory.getCurrentSession();
		return (AppConfig) session.createCriteria(AppConfig.class)
				.add(Restrictions.eq("name", name)).uniqueResult();
	}

	@Override
	@Transactional(readOnly = true)
	public Date getDBNow() {
		Session session = sessionFactory.getCurrentSession();
		return (Date) session.createSQLQuery("SELECT now() FROM dual").uniqueResult();
	}
}
