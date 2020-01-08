package image.hbm.repository;

import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.repository.AlbumRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/3/14
 * Time: 10:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AlbumRepositoryImpl implements AlbumRepository {
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	@Transactional
	public List<Album> findByDeletedFalseOrderByNameDesc() {
		CriteriaBuilder cb = this.sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<Album> criteria = cb.createQuery(Album.class);
		Root<Album> root = criteria.from(Album.class);
		criteria.select(root)
				.where(cb.equal(root.get("deleted"), false))
				.orderBy(cb.desc(root.get("name")));
		Query<Album> q = this.sessionFactory.getCurrentSession().createQuery(criteria);
		return q.setCacheable(true).list();
	}

	@Override
	@Transactional
	public Album createByName(String name) {
		Album album = new Album(name);
		this.sessionFactory.getCurrentSession().persist(album);
		return album;
	}

	@Override
	@Transactional
	public void persist(Album album) {
		this.sessionFactory.getCurrentSession().persist(album);
	}

	@Override
	@Transactional
	public void deleteById(Integer id) {
		Album album = this.sessionFactory.getCurrentSession().load(Album.class, id);
		this.sessionFactory.getCurrentSession().delete(album);
	}

	/**
	 * Returned with the intention to be an immutable object or at least
	 * not modifiable for the cache-related parts (any property except images).
	 * When modified the cache would be evicted (because Album props CacheEvict).
	 *
	 * @param id
	 * @return
	 */
	@Override
	@Transactional
	public Album getById(Integer id) {
//		logger.debug("BEGIN id = {}", id);
		// get initializes entity
		return this.sessionFactory.getCurrentSession().get(Album.class, id);
	}

	/**
	 * This Album comes from a query-cache which is evicted by e.g. ImageService.changeRating.
	 * <p>
	 * Scenario (with browser cache disabled):
	 * 1. ImageService.changeRating sets album.lastModified = 2018:02:04 20:25:34.240
	 * 2. mysql saves 2018:02:04 20:25:34.000 (without 240 milliseconds!)
	 * 3. AlbumExporterCtrl.updateJsonFor1Album (/updateJsonForAlbum) calls findAlbumByName
	 * 3. findAlbumByName sets album.lastModified = 2018:02:04 20:25:34.000
	 * 4. AlbumRepository.clearDirtyForAlbum will fail with optimistic lock because is using 2018:02:04 20:25:34.240!
	 * 5. I guess there's a rule that invalidates the cache for the specific entity (Album for this case) involved with a failed transaction.
	 * 6. Next time the same Album is required it is loaded from DB (so it has the DB value, e.g. 2018:02:04 20:25:34.000).
	 *
	 * @param name
	 * @return
	 */
	@Override
	@Transactional
	public Album findAlbumByName(String name) {
//		logger.debug("BEGIN name = {}", name);
		Session session = this.sessionFactory.getCurrentSession();
		return (Album) session.createCriteria(Album.class)
				.setCacheable(true).add(Restrictions.eq("name", name))
				.uniqueResult();
	}

	@Override
	@Transactional
	public boolean putAlbumCover(Integer imageId) {
		Session session = this.sessionFactory.getCurrentSession();
		Image newCover = session.load(Image.class, imageId);
		Album album = newCover.getAlbum();
		Image currentCover = album.getCover();
		if (currentCover == null) {
			album.setCover(newCover);
			album.setDirty(true);
			return true;
		}
		if (currentCover.getId().equals(imageId)) {
			return false;
		}
		album.setCover(newCover);
		album.setDirty(true);
		return true;
	}

	/**
	 * Used only by the below subscription:
	 * imageEventsEmitter.imageEventsByType(... EImageEventType.DELETED ...)
	 * otherwise this.evictAlbumCache or @CacheEvict (for public method) must be used.
	 *
	 * @param albumId
	 * @return
	 */
	@Override
	@Transactional
	public boolean removeAlbumCover(Integer albumId) {
		Album album = this.getById(albumId);
		// NPE when album is NULL
		if (album.getCover() == null) {
			return false;
		}
		album.setCover(null);
		return true;
	}

	/**
	 * http://www.baeldung.com/hibernate-second-level-cache
	 * <p>
	 * DML-style HQL (insert, update and delete HQL statements) invalidates all Album cache, e.g.:
	 * -    "UPDATE Album SET dirty = false WHERE id = :albumId AND dirty = true"
	 */
	@Override
	@Transactional
	public boolean clearDirtyForAlbum(Integer albumId) {
//		logger.debug("BEGIN");
		Album album = this.getById(albumId);
		// check solved by hibernate BytecodeEnhancement (+hibernate-enhance-maven-plugin)
		if (!album.isDirty()) {
//			logger.debug("END dirty update cancelled (already false)");
			return false;
		}
		album.setDirty(false);
//		logger.debug("END dirty set to false, {}", sdf.format(album.getLastUpdate()));
		return true;
	}

	/**
	 * Is about which is the latest time when an AlbumCover was modified.
	 * A last-modified date change might set dirty to false
	 * so while album is AlbumCover is no longer dirty.
	 * <p>
	 * returning Timestamp because Timestamp.getTime() adds nanos
	 *
	 * @return
	 */
	@Override
	@Transactional(readOnly = true)
	public Date getAlbumCoversLastUpdateDate() {
		Session session = this.sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Date> criteria = cb.createQuery(Date.class);
		Root<Album> root = criteria.from(Album.class);
		criteria.select(cb.greatest(root.get(this.lastUpdate())));
		Query<Date> q = session.createQuery(criteria);
		return q.setCacheable(true).getSingleResult();
	}

	private SingularAttribute<Album, Date> lastUpdate() {
		Session session = this.sessionFactory.getCurrentSession();
		EntityType<Album> type = session.getMetamodel().entity(Album.class);
		return type.getDeclaredSingularAttribute("lastUpdate", Date.class);
	}
}
