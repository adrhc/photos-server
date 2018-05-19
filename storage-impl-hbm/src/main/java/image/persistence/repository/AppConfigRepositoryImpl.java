package image.persistence.repository;

import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

/**
 * Created by adr on 2/10/18.
 */
@Service
public class AppConfigRepositoryImpl implements AppConfigRepository {
	@Inject
	private SessionFactory sessionFactory;

	@Override
	@Transactional
	public void createAppConfig(AppConfig appConfig) {
		this.sessionFactory.getCurrentSession().persist(appConfig);
	}

	@Override
	@Transactional
	public void deleteAppConfig(AppConfigEnum ace) {
		AppConfig appConfig = getAppConfigByName(ace.getValue());
		this.sessionFactory.getCurrentSession().delete(appConfig);
	}

	@Override
	@Transactional
	public void deleteById(Integer id) {
		AppConfig ac = this.sessionFactory.getCurrentSession().get(AppConfig.class, id);
		this.sessionFactory.getCurrentSession().delete(ac);
	}

	private Integer getConfigInteger(AppConfigEnum ace) {
		String s = getConfig(ace);
		if (s == null) {
			return 0;
		}
		return Integer.parseInt(s);
	}

	@Override
	@Transactional
	public String getConfig(AppConfigEnum appConfigEnum) {
		return getAppConfigByName(appConfigEnum.getValue()).getValue();
	}

	@Override
	@Transactional
	public Integer getPhotosPerPage() {
		return getConfigInteger(AppConfigEnum.photos_per_page);
	}

	@Override
	@Transactional
	public String getAlbumsPath() {
		return getConfig(AppConfigEnum.albums_path);
	}

	@Override
	@Transactional
	public AppConfig getAppConfigById(Integer id) {
		return this.sessionFactory.getCurrentSession().get(AppConfig.class, id);
	}

	@Override
	@Transactional
	public AppConfig getAppConfigByName(String name) {
		CriteriaBuilder builder = this.sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<AppConfig> criteria = builder.createQuery(AppConfig.class);
		Root<AppConfig> root = criteria.from(AppConfig.class);
		criteria.select(root).where(builder.equal(root.get("name"), name));
		Query<AppConfig> q = this.sessionFactory.getCurrentSession().createQuery(criteria);
		return q.setCacheable(true).uniqueResult();
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
	@Transactional
	public void updateValue(String value, Integer appConfigId) {
		AppConfig dbAppConfig = getAppConfigById(appConfigId);
		dbAppConfig.setValue(value);
	}


	@Override
	@Transactional(readOnly = true)
	public List<AppConfig> getAppConfigs() {
		CriteriaBuilder builder = this.sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<AppConfig> criteria = builder.createQuery(AppConfig.class);
		Root<AppConfig> root = criteria.from(AppConfig.class);
		criteria.select(root);
		Query<AppConfig> q = this.sessionFactory.getCurrentSession().createQuery(criteria);
		return q.setCacheable(true).list();
	}

	@Override
	@Transactional(readOnly = true)
	public List<AppConfig> testGetNoCacheableOrderedAppConfigs() {
		Session session = this.sessionFactory.getCurrentSession();
		return (List<AppConfig>) session.createCriteria(AppConfig.class)
				.addOrder(Order.asc("name")).list();
	}

	@Override
	@Transactional(readOnly = true)
	public AppConfig testGetNoCacheableAppConfigByName(String name) {
		Session session = this.sessionFactory.getCurrentSession();
		return (AppConfig) session.createCriteria(AppConfig.class)
				.add(Restrictions.eq("name", name)).uniqueResult();
	}

	@Override
	@Transactional(readOnly = true)
	public Date getDBNow() {
		Session session = this.sessionFactory.getCurrentSession();
		return (Date) session.createSQLQuery("SELECT now() FROM dual").uniqueResult();
	}
}
