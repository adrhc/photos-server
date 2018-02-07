package image.exifweb.album;

import image.exifweb.album.events.AlbumEventsEmitter;
import image.exifweb.image.ImageService;
import image.exifweb.image.ImageUtils;
import image.exifweb.image.events.EImageEventType;
import image.exifweb.image.events.ImageEventBuilder;
import image.exifweb.image.events.ImageEventsEmitter;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;
import image.exifweb.sys.AppConfigService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;

import static image.exifweb.album.events.EAlbumEventType.JSON_UPDATED;
import static image.exifweb.image.events.EImageEventType.DELETED;
import static image.exifweb.image.events.EImageEventType.MARKED_DELETED;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/3/14
 * Time: 10:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AlbumService {
	private static final Logger logger = LoggerFactory.getLogger(AlbumService.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");
	/**
	 * Shows in addition to status=0 and printable also !deleted, hidden, personal, ugly, duplicate images.
	 * <p>
	 * image0_.status=IF(false, image0_.status, image0_.status
	 * -(image0_.status & 1)-(image0_.status & 2)-(image0_.status & 4)-(image0_.status & 8))
	 */
	private static final String VIEW_HIDDEN_SQL =
			"AND i.status = IF(:viewHidden, i.status, i.status " +
					"- i.hidden - i.personal - i.ugly - i.duplicate) ";
	/**
	 * Shows only printable images.
	 */
	private static final String VIEW_PRINTABLE_SQL =
			"AND i.status = IF(:viewOnlyPrintable, 16, i.status) ";
	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private AppConfigService appConfigService;
	@Inject
	private ImageService imageService;
	@Inject
	private AlbumEventsEmitter albumEventsEmitter;
	@Inject
	private ImageEventsEmitter imageEventsEmitter;
	@Inject
	private ImageUtils imageUtils;

	@Transactional
	public List<Album> getAlbums() {
		return sessionFactory.getCurrentSession().createCriteria(Album.class)
				.setCacheable(true)
				.add(Restrictions.eq("deleted", false))
				.addOrder(Order.desc("name")).list();
	}

	@Transactional
	public Album create(String name) {
		Album album = new Album(name);
		sessionFactory.getCurrentSession().persist(album);
		return album;
	}

	/**
	 * Returned with the intention to be an immutable object or at least
	 * not modifiable for the cache-related parts (any property except images).
	 * When modified the cache would be evicted (because Album props CacheEvict).
	 *
	 * @param id
	 * @return
	 */
	@Transactional
	public Album getAlbumById(Integer id) {
//		logger.debug("BEGIN id = {}", id);
		// get initializes entity
		return (Album) sessionFactory.getCurrentSession().get(Album.class, id);
	}

	/**
	 * This Album comes from a query-cache which is evicted by e.g. ImageService.changeRating.
	 * <p>
	 * Scenario (with browser cache disabled):
	 * 1. ImageService.changeRating sets album.lastModified = 2018:02:04 20:25:34.240
	 * 2. mysql saves 2018:02:04 20:25:34.000 (without 240 milliseconds!)
	 * 3. AlbumCtrl.updateJsonFor1Album (/updateJsonForAlbum) calls getAlbumByName
	 * 3. getAlbumByName sets album.lastModified = 2018:02:04 20:25:34.000
	 * 4. AlbumService.clearDirtyForAlbum will fail with optimistic lock because is using 2018:02:04 20:25:34.240!
	 * 5. I guess there's a rule that invalidates the cache for the specific entity (Album for this case) involved with a failed transaction.
	 * 6. Next time the same Album is required it is loaded from DB (so it has the DB value, e.g. 2018:02:04 20:25:34.000).
	 *
	 * @param name
	 * @return
	 */
	@Transactional
	public Album getAlbumByName(String name) {
//		logger.debug("BEGIN name = {}", name);
		Session session = sessionFactory.getCurrentSession();
		return (Album) session.createCriteria(Album.class)
				.setCacheable(true).add(Restrictions.eq("name", name))
				.uniqueResult();
	}

	@Transactional(readOnly = true)
	public int getPageCount(String toSearch, boolean viewHidden,
	                        boolean viewOnlyPrintable, Integer albumId) {
		Session session = sessionFactory.getCurrentSession();
		Query q;
		if (StringUtils.hasText(toSearch)) {
			q = session.createQuery("SELECT count(i) FROM Image i " +
					(albumId == -1 ? "WHERE i.deleted = 0 " : "JOIN i.album a WHERE a.id = :albumId AND i.deleted = 0 ") +
					VIEW_HIDDEN_SQL + VIEW_PRINTABLE_SQL +
					"AND i.name LIKE :toSearch");
			// searches case-sensitive for name!
			q.setParameter("toSearch", "%" + toSearch + "%");
		} else {
			q = session.createQuery("SELECT count(i) FROM Image i JOIN i.album a " +
					"WHERE a.id = :albumId " +
					"AND i.deleted = 0 " +
					VIEW_HIDDEN_SQL + VIEW_PRINTABLE_SQL);
			q.setCacheable(!viewHidden && !viewOnlyPrintable);
		}
		if (albumId != -1) {
			q.setInteger("albumId", albumId);
		}
		q.setBoolean("viewHidden", viewHidden);
		q.setBoolean("viewOnlyPrintable", viewOnlyPrintable);
		return Double.valueOf(Math.ceil(((Number) q.uniqueResult()).doubleValue() /
				appConfigService.getPhotosPerPage())).intValue();

	}

	/**
	 * http://www.baeldung.com/hibernate-second-level-cache
	 * - For all tables that are queried as part of cacheable queries, Hibernate keeps last update timestamps ...
	 * <p>
	 * ehcache logs for Album update:
	 * - Pre-invalidating space [Album], timestamp: 6216125958041600
	 * - Invalidating space [Album], timestamp: 6216125712445440
	 * ehcache logs for a page-count query:
	 * - Checking query spaces are up-to-date: [Album, Image]
	 * - [Album] last update timestamp: 6216125712445440, result set timestamp: 6216124363251712
	 * -         Cached query results were not up-to-date
	 * so when Album or Image cache is not up to date this query becomes invalid.
	 */
	@Transactional(readOnly = true)
	public List<AlbumPage> getPageFromDb(int pageNr, String sort, String toSearch,
	                                     boolean viewHidden, boolean viewOnlyPrintable,
	                                     Integer albumId) {
		Session session = sessionFactory.getCurrentSession();
		Query q;
		if (StringUtils.hasText(toSearch)) {
			q = session.createQuery("SELECT new image.exifweb.album.AlbumPage(" +
					"i.id, i.name, i.hidden, i.personal, i.ugly, i.duplicate, " +
					"i.printable, i.imageHeight, i.imageWidth, i.rating, a.cover.id, " +
					"i.thumbLastModified, i.dateTime, a.name, i.lastUpdate) " +
//					"thumbPath(a.name, i.thumbLastModified, i.name), " +
//					"imagePath(a.name, i.thumbLastModified, i.name)) " +
					"FROM Image i JOIN i.album a " +
					(albumId == -1 ? "WHERE i.deleted = 0 " : "JOIN i.album a WHERE a.id = :albumId AND i.deleted = 0 ") +
					VIEW_HIDDEN_SQL + VIEW_PRINTABLE_SQL +
					"AND i.name LIKE :toSearch " +
					"ORDER BY i.dateTimeOriginal " + sort);
			// searches case-sensitive for name!
			q.setString("toSearch", "%" + toSearch + "%");
		} else {
			q = session.createQuery("SELECT new image.exifweb.album.AlbumPage(" +
					"i.id, i.name, i.hidden, i.personal, i.ugly, i.duplicate, " +
					"i.printable, i.imageHeight, i.imageWidth, i.rating, a.cover.id, " +
					"i.thumbLastModified, i.dateTime, a.name, i.lastUpdate) " +
//					"thumbPath(a.name, i.thumbLastModified, i.name), " +
//					"imagePath(a.name, i.thumbLastModified, i.name)) " +
					"FROM Image i JOIN i.album a " +
					"WHERE a.id = :albumId AND i.deleted = 0 " +
					VIEW_HIDDEN_SQL + VIEW_PRINTABLE_SQL +
					"ORDER BY i.dateTimeOriginal " + sort);
			q.setCacheable(!viewHidden && !viewOnlyPrintable);
		}
		if (albumId != -1) {
			q.setInteger("albumId", albumId);
		}
		q.setBoolean("viewHidden", viewHidden);
		q.setBoolean("viewOnlyPrintable", viewOnlyPrintable);
		q.setFirstResult((pageNr - 1) * appConfigService.getPhotosPerPage());
		q.setMaxResults(appConfigService.getPhotosPerPage());
		return q.list();
	}

	public List<AlbumPage> getPage(int pageNr, String sort, String toSearch,
	                               boolean viewHidden, boolean viewOnlyPrintable,
	                               Integer albumId) {
		List<AlbumPage> thumbs = getPageFromDb(pageNr, sort,
				toSearch, viewHidden, viewOnlyPrintable, albumId);
		imageUtils.appendImageDimensions(thumbs);
		imageUtils.appendImagePaths(thumbs);
		return thumbs;
	}

	/**
	 * Cached Album is detached so can't be used as persistent as required in this method.
	 *
	 * @param foundImageNames
	 */
	@Transactional
	public void deleteNotFoundImages(List<String> foundImageNames, Album album) {
		logger.debug("BEGIN {}", album.getName());
		List<Image> images = imageService.getImagesByAlbumId(album.getId());
		images.forEach(image -> {
			String dbName = image.getName();
			int fsNameIdx = foundImageNames.indexOf(dbName);
			if (fsNameIdx >= 0) {
				// imagine existenta in DB cu acelas nume ca in file system
				return;
			}
			String oppositeExtensionCase = toFileNameWithOppositeExtensionCase(dbName);
			fsNameIdx = foundImageNames.indexOf(oppositeExtensionCase);
			ImageEventBuilder imgEvBuilder = new ImageEventBuilder().album(album).image(image);
			if (fsNameIdx >= 0) {
				logger.debug("poza din DB ({}) cu nume diferit in file system ({}): actualizez in DB cu {}",
						dbName, oppositeExtensionCase, oppositeExtensionCase);
				image.setName(oppositeExtensionCase);
				imageEventsEmitter.emit(imgEvBuilder.type(EImageEventType.UPDATED).build());
				return;
			}
			if (image.getStatus().equals(Image.DEFAULT_STATUS)) {
				// status = 0
				logger.debug("poza din DB ({}) nu exista in file system: sterg din DB", dbName);
				checkAndRemoveAlbumCover(image, album);
				imageService.removeNoTx(image);
				imageEventsEmitter.emit(imgEvBuilder.type(DELETED).build());
				return;
			}
			// status != 0 (adica e o imagine "prelucrata")
			logger.debug("poza din DB ({}) nu exista in file system: marchez ca stearsa", dbName);
			checkAndRemoveAlbumCover(image, album);
			image.setDeleted(true);
			imageEventsEmitter.emit(imgEvBuilder.type(MARKED_DELETED).build());
		});
		logger.debug("END {}", album.getName());
	}

	private String toFileNameWithOppositeExtensionCase(String fileName) {
		StringBuilder sb = new StringBuilder(fileName);
		int idx = sb.lastIndexOf(".");
		if (idx <= 0) {
			return fileName;
		}
		sb.append(fileName.substring(0, idx));
		String pointAndExtension = fileName.substring(idx);
		if (pointAndExtension.equals(pointAndExtension.toLowerCase())) {
			sb.append(pointAndExtension.toUpperCase());
		} else {
			sb.append(pointAndExtension.toLowerCase());
		}
		return sb.toString();
	}

	@Transactional
	public boolean putAlbumCover(Integer imageId) {
		Session session = sessionFactory.getCurrentSession();
		Image newCover = (Image) session.load(Image.class, imageId);
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
	@Transactional
	public boolean clearDirtyForAlbum(Integer albumId) {
		logger.debug("BEGIN");
		Album album = getAlbumById(albumId);
		// check solved by hibernate BytecodeEnhancement (+hibernate-enhance-maven-plugin)
		if (!album.isDirty()) {
			logger.debug("END dirty update cancelled (already false), {}",
					sdf.format(album.getLastUpdate()));
			return false;
		}
		album.setDirty(false);
		logger.debug("END dirty set to false, {}", sdf.format(album.getLastUpdate()));
		return true;
	}

	private void checkAndRemoveAlbumCover(Image image, Album album) {
		if (!isImageTheCoverForAlbum(image, album)) {
			return;
		}
		try {
			removeAlbumCover(album.getId());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error("[DELETED, MARKED_DELETED] removeAlbumCover\n", album.toString());
		}
	}

	private boolean isImageTheCoverForAlbum(Image image, Album album) {
		return album.getCover() != null && album.getCover().getId().equals(image.getId());
	}

	@PostConstruct
	public void postConstruct() {
		// album's json files updated
		albumEventsEmitter.subscribeAsync(JSON_UPDATED,
				ae -> {
					if (ae.getAlbum() == null) {
						logger.error("[JSON_UPDATED] album is null");
						return false;
					} else {
						logger.debug("[JSON_UPDATED] album id = {}, name = {}",
								ae.getAlbum().getId(), ae.getAlbum().getName());
						return true;
					}
				},
				ae -> {
					// on error the subscription will be disposed!
					// this try ... catch protects against that
					try {
						clearDirtyForAlbum(ae.getAlbum().getId());
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						logger.error("[JSON_UPDATED] clearDirtyForAlbum\n", ae.toString());
					}
				});
		logger.debug("END");
	}
}