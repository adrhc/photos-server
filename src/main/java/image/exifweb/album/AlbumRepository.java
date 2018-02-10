package image.exifweb.album;

import image.exifweb.image.ImageService;
import image.exifweb.image.events.EImageEventType;
import image.exifweb.image.events.ImageEventBuilder;
import image.exifweb.image.events.ImageEventsEmitter;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;
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
public class AlbumRepository {
	private static final Logger logger = LoggerFactory.getLogger(AlbumRepository.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");
	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private ImageService imageService;
	@Inject
	private ImageEventsEmitter imageEventsEmitter;

	@Transactional
	public List<Album> getAlbumsOrderedByName() {
		return sessionFactory.getCurrentSession()
				.createCriteria(Album.class).setCacheable(true)
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
	 * 3. AlbumExporterCtrl.updateJsonFor1Album (/updateJsonForAlbum) calls getAlbumByName
	 * 3. getAlbumByName sets album.lastModified = 2018:02:04 20:25:34.000
	 * 4. AlbumRepository.clearDirtyForAlbum will fail with optimistic lock because is using 2018:02:04 20:25:34.240!
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
			logger.debug("END dirty update cancelled (already false)");
			return false;
		}
		album.setDirty(false);
		logger.debug("END dirty set to false, {}", sdf.format(album.getLastUpdate()));
		return true;
	}

	/**
	 * When image is cover for album then remove album's cover (set it to null).
	 *
	 * @param image
	 * @param album
	 */
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
}
