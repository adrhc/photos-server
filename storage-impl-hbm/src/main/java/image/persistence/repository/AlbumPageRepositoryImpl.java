package image.persistence.repository;

import image.cdm.album.page.AlbumPage;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by adr on 2/8/18.
 */
@Service
public class AlbumPageRepositoryImpl implements AlbumPageRepository {
	/**
	 * Shows in addition to status=0 and printable also !deleted, hidden, personal, ugly, duplicate images.
	 * <p>
	 * image0_.status=IF(:viewHidden, image0_.status, image0_.status
	 * -(image0_.status & 1)-(image0_.status & 2)-(image0_.status & 4)-(image0_.status & 8))
	 */
	private static final String VIEW_HIDDEN_SQL =
			"AND (:viewHidden = true OR i.flags.hidden = false AND i.flags.personal = false AND i.flags.ugly = false AND i.flags.duplicate = false) ";
	/**
	 * Shows only printable images.
	 */
	private static final String VIEW_PRINTABLE_SQL = "AND (:viewOnlyPrintable = false OR i.flags.printable = true) ";
	private static final Integer NULL_ALBUM_ID = -1;

	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private AppConfigRepository appConfigRepository;

	@Override
	@Transactional(readOnly = true)
	public int getPageCount(String toSearch, boolean viewHidden,
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
					(emptyAlbumId ? "" : "JOIN i.album a ") +
					"WHERE " + (emptyAlbumId ? "" : "a.id = :albumId AND ") +
					"i.deleted = false " +
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
					"a.name, i.lastUpdate) " +
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
					"a.name, i.lastUpdate) " +
//					"thumbPath(a.name, i.imageMetadata.thumbLastModified, i.name), " +
//					"imagePath(a.name, i.imageMetadata.thumbLastModified, i.name)) " +
					"FROM Image i " +
					(emptyAlbumId ? "" : "JOIN i.album a ") +
					"WHERE " + (emptyAlbumId ? "" : "a.id = :albumId AND ") +
					"i.deleted = false " +
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
}
