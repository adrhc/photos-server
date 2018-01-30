package image.exifweb.album;

import image.exifweb.album.cache.IAlbumCache;
import image.exifweb.album.events.AlbumEventsEmitter;
import image.exifweb.album.events.EAlbumEventType;
import image.exifweb.image.ImageDimensions;
import image.exifweb.image.ImageService;
import image.exifweb.image.ImageThumb;
import image.exifweb.image.events.EImageEventType;
import image.exifweb.image.events.ImageEvent;
import image.exifweb.image.events.ImageEventBuilder;
import image.exifweb.image.events.ImageEventsEmitter;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;
import image.exifweb.persistence.view.AlbumCover;
import image.exifweb.sys.AppConfigService;
import io.reactivex.Observable;
import org.apache.commons.lang.text.StrBuilder;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/3/14
 * Time: 10:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AlbumService implements IAlbumCache {
	private static final Logger logger = LoggerFactory.getLogger(AlbumService.class);

	@Value("${thumbs.dir}")
	private String thumbsDir;
	@Value("${max.thumb.size.px}")
	private String maxThumbSizePx;
	@Value("${max.thumb.size}")
	private double maxThumbSize;
	@Value("${max.thumb.size}")
	private int maxThumbSizeInt;
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

	/**
	 * Returned with the intention to be an immutable object or at least
	 * not modifiable for the cache-related parts (any property except images).
	 * When modified the cache would be evicted (because Album props CacheEvict).
	 *
	 * @param name
	 * @return
	 */
	@Caching(put = {
			@CachePut(value = "album", unless = "#result == null", key = "#result.id"),
			@CachePut(value = "album", unless = "#result == null", key = "#result.name")
	})
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
	@Cacheable(value = "album", unless = "#result == null", key = "#id")
	@Transactional(readOnly = true)
	public Album getAlbumById(Integer id) {
		logger.debug("BEGIN id = {}", id);
		Session session = sessionFactory.getCurrentSession();
		// get initializes entity
		return (Album) session.get(Album.class, id);
	}

	@Cacheable(value = "album", unless = "#result == null", key = "#name")
	@Transactional
	public Album getAlbumByName(String name) {
		logger.debug("BEGIN name = {}", name);
		Session session = sessionFactory.getCurrentSession();
		return (Album) session.createCriteria(Album.class)
				.add(Restrictions.eq("name", name)).uniqueResult();
	}

	@Cacheable(value = "covers", key = "'albumCoversLastUpdateDate'")
	@Transactional
	public Date getAlbumCoversLastUpdateDate() {
		logger.debug("BEGIN");
		Session session = sessionFactory.getCurrentSession();
		return (Date) session.createCriteria(Album.class)
				.setProjection(Projections.max("lastUpdate"))
				.uniqueResult();
	}

	@Cacheable(value = "covers", key = "'allCovers'")
	public List<AlbumCover> getAllCovers() {
		logger.debug("BEGIN");
		List<AlbumCover> covers = loadAllCovers();
		prepareImageDimensions(covers);
		prepareURI(covers);
		return covers;
	}

	@Transactional
	private List<AlbumCover> loadAllCovers() {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("FROM AlbumCover ORDER BY albumName DESC");
		return q.list();
	}

	@Transactional
	public int getPageCount(String toSearch, boolean viewHidden, Integer albumId) {
		Session session = sessionFactory.getCurrentSession();
		Query q;
		if (StringUtils.hasText(toSearch)) {
			q = session.createQuery("SELECT count(i) FROM Image i " +
					(albumId == -1 ? "WHERE i.deleted = 0 " : "JOIN i.album a WHERE a.id = :albumId AND i.deleted = 0 ") +
					"AND i.status = IF(:viewHidden, i.status, 0) " +
					"AND i.name LIKE :toSearch");
			// searches case-sensitive for name!
			q.setParameter("toSearch", "%" + toSearch + "%");
		} else {
			q = session.createQuery("SELECT count(i) FROM Image i JOIN i.album a " +
					"WHERE a.id = :albumId " +
					"AND i.deleted = 0 " +
					"AND i.status = IF(:viewHidden, i.status, 0)");
		}
		if (albumId != -1) {
			q.setInteger("albumId", albumId);
		}
		q.setBoolean("viewHidden", viewHidden);
		return Double.valueOf(Math.ceil(((Number) q.uniqueResult()).doubleValue() /
				appConfigService.getPhotosPerPage())).intValue();

	}

	@Transactional
	public List<PhotoThumb> getPageFromDb(int pageNr, String sort, String toSearch,
	                                      boolean viewHidden, Integer albumId) {
		Session session = sessionFactory.getCurrentSession();
		Query q;
		if (StringUtils.hasText(toSearch)) {
			q = session.createQuery("SELECT new image.exifweb.album.PhotoThumb(" +
					"i.id, i.name, i.hidden, i.personal, i.ugly, i.duplicate, " +
					"i.imageHeight, i.imageWidth, i.rating, a.cover.id, " +
					"i.thumbLastModified, i.dateTime, a.name as albumName) " +
					"FROM Image i JOIN i.album a " +
					(albumId == -1 ? "WHERE i.deleted = 0 " : "JOIN i.album a WHERE a.id = :albumId AND i.deleted = 0 ") +
					"AND i.status = IF(:viewHidden, i.status, 0) " +
					"AND i.name LIKE :toSearch " +
					"ORDER BY i.dateTimeOriginal " + sort);
			// searches case-sensitive for name!
			q.setString("toSearch", "%" + toSearch + "%");
		} else {
			q = session.createQuery("SELECT new image.exifweb.album.PhotoThumb(" +
					"i.id, i.name, i.hidden, i.personal, i.ugly, i.duplicate, " +
					"i.imageHeight, i.imageWidth, i.rating, a.cover.id, " +
					"i.thumbLastModified, i.dateTime, a.name as albumName) " +
					"FROM Image i JOIN i.album a " +
					"WHERE a.id = :albumId AND i.deleted = 0 " +
					"AND i.status = IF(:viewHidden, i.status, 0) " +
					"ORDER BY i.dateTimeOriginal " + sort);
		}
		if (albumId != -1) {
			q.setInteger("albumId", albumId);
		}
		q.setBoolean("viewHidden", viewHidden);
		q.setFirstResult((pageNr - 1) * appConfigService.getPhotosPerPage());
		q.setMaxResults(appConfigService.getPhotosPerPage());
		return q.list();
	}

	public List<PhotoThumb> getPage(int pageNr, String sort, String toSearch,
	                                boolean viewHidden, Integer albumId) {
		List<PhotoThumb> thumbs = getPageFromDb(pageNr, sort, toSearch, viewHidden, albumId);
		prepareImageDimensions(thumbs);
		prepareURI(thumbs);
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
			if (fsNameIdx >= 0) {
				logger.debug("poza din DB ({}) cu nume diferit in file system ({}): actualizez in DB cu {}",
						dbName, oppositeExtensionCase, oppositeExtensionCase);
				image.setName(oppositeExtensionCase);
				imageEventsEmitter.emit(ImageEventBuilder.of(EImageEventType.UPDATED)
						.image(image).album(album).build());
				return;
			}
			if (image.getStatus().equals(Image.DEFAULT_STATUS)) {
				// status = 0
				logger.debug("poza din DB ({}) nu exista in file system: sterg din DB", dbName);
				imageService.remove(image);
				imageEventsEmitter.emit(ImageEventBuilder.of(EImageEventType.DELETED)
						.image(image).album(album).build());
				return;
			}
			// status != 0 (adica e o imagine "prelucrata")
			logger.debug("poza din DB ({}) nu exista in file system: marchez ca stearsa", dbName);
			image.setDeleted(true);
			imageEventsEmitter.emit(ImageEventBuilder.of(EImageEventType.UPDATED)
					.image(image).album(album).build());
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

	private void prepareURI(List<? extends ImageThumb> thumbs) {
		StrBuilder thumbPath = new StrBuilder(64);
		for (ImageThumb thumb : thumbs) {
			if (thumb.getImgName() != null) {
				// 'thumbs'/albumName/thumbLastModified/imgName
				thumbPath.append(thumbsDir).append('/');
				thumbPath.append(thumb.getAlbumName()).append('/');
				thumbPath.append(thumb.getThumbLastModified().getTime()).append('/');
				thumbPath.append(thumb.getImgName());
				thumb.setImagePath(thumbPath.toString());
				thumbPath.clear();
			}
		}
	}

	private void prepareImageDimensions(List<? extends ImageDimensions> imageDimensions) {
		for (ImageDimensions row : imageDimensions) {
			if (row.getImageHeight() < row.getImageWidth()) {
				row.setImageHeight((int) Math.floor(maxThumbSize * row.getImageHeight() / row.getImageWidth()));
				row.setImageWidth(maxThumbSizeInt);
			} else {
				row.setImageWidth((int) Math.floor(maxThumbSize * row.getImageWidth() / row.getImageHeight()));
				row.setImageHeight(maxThumbSizeInt);
			}
		}
	}

	@Transactional
//    @CacheEvict(value = "covers", allEntries = true)
	public void putAlbumCover(Integer imageId) {
		Session session = sessionFactory.getCurrentSession();
		Image image = (Image) session.load(Image.class, imageId);
		Album album = image.getAlbum();
		album.setCover(image);
		album.setDirty(true);
	}

	/**
	 * Used only by the below subscription:
	 * imageEventsEmitter.imageEventsByType(... EImageEventType.DELETED ...)
	 * otherwise this.evictAlbumCache or @CacheEvict (for public method) must be used.
	 *
	 * @param album
	 * @return
	 */
	@Transactional
	private boolean removeAlbumCover(Album album) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("UPDATE Album SET cover = NULL " +
				"WHERE id = :albumId AND cover IS NOT NULL");
		q.setParameter("albumId", album.getId());
		return q.executeUpdate() > 0;
	}

	//    @CacheEvict(value = "covers", allEntries = true)
	@Transactional
	private void clearDirtyForAlbum(Integer albumId) {
		Session session = sessionFactory.getCurrentSession();
		Album album = (Album) session.get(Album.class, albumId);
		if (album.isDirty()) {
			// avoid db update when not dirty
			// avoid evicting cache when not dirty
			album.setDirty(false);
		}
	}

	@PostConstruct
	public void postConstruct() {
		// cover image changed (dealt with below)
		Observable<Album> coverImgChanged = imageEventsEmitter.imageEventsByType(
				EnumSet.of(EImageEventType.THUMB_UPDATED, EImageEventType.EXIF_UPDATED))
				.filter(ie -> ie.getImage().isCover())
				.map(ie -> ie.getImage().getAlbum());
		// cover image deleted
		Observable<Album> coverImgDeleted = imageEventsEmitter.imageEventsByType(
				EnumSet.of(EImageEventType.DELETED))
				.filter(ie -> ie.getAlbum().getCover() != null)
				.filter(ie ->
						ie.getAlbum().getCover().getId().equals(ie.getImage().getId()))
				.map(ImageEvent::getAlbum)
				.filter(this::removeAlbumCover);
		// cover image changed or deleted
		coverImgDeleted.mergeWith(coverImgChanged)
				.doOnNext(this::evictAlbumCache)
				.subscribe(album -> this.evictCoversCache());
		// album's json files updated
		albumEventsEmitter.subscribe(EAlbumEventType.JSON_UPDATED,
				ae -> clearDirtyForAlbum(ae.getAlbum().getId()));
	}
}