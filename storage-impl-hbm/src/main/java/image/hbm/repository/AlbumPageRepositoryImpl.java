package image.hbm.repository;

import image.cdm.album.page.AlbumPage;
import image.persistence.repository.AlbumPageRepository;
import image.persistence.repository.AppConfigRepository;
import image.persistence.repository.ESortType;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by adr on 2/8/18.
 */
@Service
public class AlbumPageRepositoryImpl implements AlbumPageRepository {
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private AppConfigRepository appConfigRepository;

	@Override
	@Transactional(readOnly = true)
	public int countPages(String toSearch, boolean viewHidden,
			boolean viewOnlyPrintable, Integer albumId) {
		boolean emptyAlbumId = albumId == null || albumId.equals(NULL_ALBUM_ID);
		Session session = this.sessionFactory.getCurrentSession();
		Query q;
		if (StringUtils.hasText(toSearch)) {
			q = session.createQuery("SELECT count(i) FROM Image i " +
					(emptyAlbumId ? "WHERE i.deleted = false " :
							"JOIN i.album a WHERE a.id = :albumId AND i.deleted = false ") +
					VIEW_HIDDEN_SQL + VIEW_PRINTABLE_SQL +
					"AND i.name LIKE :toSearch");
			// searches case-sensitive for name!
			q.setParameter("toSearch", "%" + toSearch + "%");
		} else {
			q = session.createQuery("SELECT count(i) FROM Image i " +
					(emptyAlbumId ? "WHERE i.deleted = false " :
							"JOIN i.album a WHERE a.id = :albumId AND i.deleted = false ") +
					VIEW_HIDDEN_SQL + VIEW_PRINTABLE_SQL);
			q.setCacheable(!viewHidden && !viewOnlyPrintable);
		}
		if (!emptyAlbumId) {
			q.setInteger("albumId", albumId);
		}
		q.setBoolean("viewHidden", viewHidden);
		q.setBoolean("viewOnlyPrintable", viewOnlyPrintable);
		return Double.valueOf(Math.ceil(((Number) q.uniqueResult()).doubleValue() /
				this.appConfigRepository.getPhotosPerPage())).intValue();
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
	@Override
	@Transactional(readOnly = true)
	public List<AlbumPage> getPageFromDb(int pageNr, ESortType sort, String toSearch,
			boolean viewHidden, boolean viewOnlyPrintable,
			Integer albumId) {
		boolean emptyAlbumId = albumId == null || albumId.equals(NULL_ALBUM_ID);
		Session session = this.sessionFactory.getCurrentSession();
		Query q;
		if (StringUtils.hasText(toSearch)) {
			q = session.createQuery("SELECT new image.cdm.album.page.AlbumPage(" +
					"i.id, i.name, i.flags.hidden, i.flags.personal, i.flags.ugly, i.flags.duplicate, " +
					"i.flags.printable, i.imageMetadata.exifData.imageHeight, " +
					"i.imageMetadata.exifData.imageWidth, i.rating, a.cover.id, " +
					"i.imageMetadata.thumbLastModified, i.imageMetadata.dateTime, " +
					"i.lastUpdate, a.name, a.lastUpdate) " +
//					"thumbPath(a.name, i.imageMetadata.thumbLastModified, i.name), " +
//					"imagePath(a.name, i.imageMetadata.thumbLastModified, i.name)) " +
					"FROM Image i JOIN i.album a " +
					(emptyAlbumId ? "WHERE i.deleted = false " : "JOIN i.album a WHERE a.id = :albumId AND i.deleted = false ") +
					VIEW_HIDDEN_SQL + VIEW_PRINTABLE_SQL +
					"AND i.name LIKE :toSearch " +
					"ORDER BY i.imageMetadata.exifData.dateTimeOriginal " + sort);
			// searches case-sensitive for name!
			q.setString("toSearch", "%" + toSearch + "%");
		} else {
			q = session.createQuery("SELECT new image.cdm.album.page.AlbumPage(" +
					"i.id, i.name, i.flags.hidden, i.flags.personal, i.flags.ugly, i.flags.duplicate, " +
					"i.flags.printable, i.imageMetadata.exifData.imageHeight, " +
					"i.imageMetadata.exifData.imageWidth, i.rating, a.cover.id, " +
					"i.imageMetadata.thumbLastModified, i.imageMetadata.dateTime, " +
					"i.lastUpdate, a.name, a.lastUpdate) " +
//					"thumbPath(a.name, i.imageMetadata.thumbLastModified, i.name), " +
//					"imagePath(a.name, i.imageMetadata.thumbLastModified, i.name)) " +
					"FROM Image i JOIN i.album a " +
					(emptyAlbumId ? "WHERE i.deleted = false " : "JOIN i.album a WHERE a.id = :albumId AND i.deleted = false ") +
					VIEW_HIDDEN_SQL + VIEW_PRINTABLE_SQL +
					"ORDER BY i.imageMetadata.exifData.dateTimeOriginal " + sort);
			q.setCacheable(!viewHidden && !viewOnlyPrintable);
		}
		if (!emptyAlbumId) {
			q.setInteger("albumId", albumId);
		}
		q.setBoolean("viewHidden", viewHidden);
		q.setBoolean("viewOnlyPrintable", viewOnlyPrintable);
		q.setFirstResult((pageNr - 1) * this.appConfigRepository.getPhotosPerPage());
		q.setMaxResults(this.appConfigRepository.getPhotosPerPage());
		return q.list();
	}

	@Override
	public Optional<Date> getPageLastUpdate(int pageNr, String toSearch,
			boolean viewHidden, boolean viewOnlyPrintable, Integer albumId) {
		throw new UnsupportedOperationException();
	}
}
