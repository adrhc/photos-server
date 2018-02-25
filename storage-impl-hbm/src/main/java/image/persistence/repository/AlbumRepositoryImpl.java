package image.persistence.repository;

import image.persistence.entity.Album;
import image.persistence.entity.Image;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/3/14
 * Time: 10:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AlbumRepositoryImpl implements AlbumRepository {
	private static final Logger logger = LoggerFactory.getLogger(AlbumRepositoryImpl.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
	@Inject
	private SessionFactory sessionFactory;

	@Override
	@Transactional
	public List<Album> getAlbumsOrderedByName() {
		return sessionFactory.getCurrentSession()
				.createCriteria(Album.class).setCacheable(true)
				.add(Restrictions.eq("deleted", false))
				.addOrder(Order.desc("name")).list();
	}

	@Override
	@Transactional
	public Album createAlbum(String name) {
		Album album = new Album(name);
		sessionFactory.getCurrentSession().persist(album);
		return album;
	}

	@Override
	@Transactional
	public void deleteAlbum(Integer id) {
		Album album = sessionFactory.getCurrentSession().get(Album.class, id);
		sessionFactory.getCurrentSession().delete(album);
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
	public Album getAlbumById(Integer id) {
//		logger.debug("BEGIN id = {}", id);
		// get initializes entity
		return sessionFactory.getCurrentSession().get(Album.class, id);
	}

	/**
	 * This Album comes from a query-cache which is evicted by e.g. ImageService.changeRating.
	 * <p>
	 * Scenario (with browser cache disabled):
	 * 1. ImageService.changeRating sets album.lastModified = 2018:02:04 20:25:34.240
	 * 2. mysql saves 2018:02:04 20:25:34.000 (without 240 milliseconds!)
	 * 3. AlbumExporterCtrl.updateJsonFor1Album (/updateJsonForAlbum) calls getAlbumByName
	 * 3. getAlbumByName sets album.lastModified = 2018:02:04 20:25:34.000
	 * 4. AlbumRepository.clearDirtyForAlbum will fail with optimistic lock because is using 2018:02:04 20:25:34.240!
	 * 5. I guess there's a rule that invalidates the cache for the specific entity (Album for this case) involved with a failed transaction.
	 * 6. Next time the same Album is required it is loaded from DB (so it has the DB value, e.g. 2018:02:04 20:25:34.000).
	 *
	 * @param name
	 * @return
	 */
	@Override
	@Transactional
	public Album getAlbumByName(String name) {
//		logger.debug("BEGIN name = {}", name);
		Session session = sessionFactory.getCurrentSession();
		return (Album) session.createCriteria(Album.class)
				.setCacheable(true).add(Restrictions.eq("name", name))
				.uniqueResult();
	}

	@Override
	@Transactional
	public boolean putAlbumCover(Integer imageId) {
		Session session = sessionFactory.getCurrentSession();
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
		Album album = getAlbumById(albumId);
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
		logger.debug("BEGIN");
		Album album = getAlbumById(albumId);
		// check solved by hibernate BytecodeEnhancement (+hibernate-enhance-maven-plugin)
		if (!album.isDirty()) {
			logger.debug("END dirty update cancelled (already false)");
			return false;
		}
		album.setDirty(false);
		logger.debug("END dirty set to false, {}", sdf.format(album.getLastUpdate()));
		return true;
	}
}
