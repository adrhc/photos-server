package image.hbm.repository;

import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.repository.AppConfigRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

/**
 * Created by adr on 2/10/18.
 */
@Component
public class AppConfigRepositoryImpl implements AppConfigRepository {
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	@Transactional
	public void persist(AppConfig appConfig) {
		this.sessionFactory.getCurrentSession().persist(appConfig);
	}

	@Override
	@Transactional
	public void deleteByEnumeratedName(AppConfigEnum ace) {
		AppConfig appConfig = findByName(ace.getValue());
		this.sessionFactory.getCurrentSession().delete(appConfig);
	}

	@Override
	@Transactional
	public void deleteById(Integer id) {
		AppConfig ac = this.sessionFactory.getCurrentSession().get(AppConfig.class, id);
		this.sessionFactory.getCurrentSession().delete(ac);
	}

	private Integer getConfigInteger(AppConfigEnum ace) {
		String s = findValueByEnumeratedName(ace);
		if (s == null) {
			return 0;
		}
		return Integer.parseInt(s);
	}

	@Override
	@Transactional
	public String findValueByEnumeratedName(AppConfigEnum appConfigEnum) {
		return findByName(appConfigEnum.getValue()).getValue();
	}

	@Override
	@Transactional
	public Integer getPhotosPerPage() {
		return getConfigInteger(AppConfigEnum.photos_per_page);
	}

	@Override
	@Transactional
	public String getAlbumsPath() {
		return findValueByEnumeratedName(AppConfigEnum.albums_path);
	}

	@Override
	@Transactional
	public AppConfig getById(Integer id) {
		return this.sessionFactory.getCurrentSession().get(AppConfig.class, id);
	}

	@Override
	@Transactional
	public AppConfig findByName(String name) {
		CriteriaBuilder builder = this.sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<AppConfig> criteria = builder.createQuery(AppConfig.class);
		Root<AppConfig> root = criteria.from(AppConfig.class);
		criteria.select(root).where(builder.equal(root.get("name"), name));
		Query<AppConfig> q = this.sessionFactory.getCurrentSession().createQuery(criteria);
		return q.setCacheable(true).uniqueResult();
	}

	@Override
	@Transactional
	public void saveAll(Iterable<AppConfig> appConfigs) {
		List<AppConfig> dbAppConfigs = findAll();
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
		AppConfig dbAppConfig = getById(appConfigId);
		dbAppConfig.setValue(value);
	}


	@Override
	@Transactional(readOnly = true)
	public List<AppConfig> findAll() {
		CriteriaBuilder builder = this.sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<AppConfig> criteria = builder.createQuery(AppConfig.class);
		Root<AppConfig> root = criteria.from(AppConfig.class);
		criteria.select(root);
		Query<AppConfig> q = this.sessionFactory.getCurrentSession().createQuery(criteria);
		return q.setCacheable(true).list();
	}

	@Override
	@Transactional(readOnly = true)
	public List<AppConfig> findAllOrderByNameAscNotCached() {
		Session session = this.sessionFactory.getCurrentSession();
		return (List<AppConfig>) session.createCriteria(AppConfig.class)
				.addOrder(Order.asc("name")).list();
	}

	@Override
	@Transactional(readOnly = true)
	public AppConfig findByNameNotCached(String name) {
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
