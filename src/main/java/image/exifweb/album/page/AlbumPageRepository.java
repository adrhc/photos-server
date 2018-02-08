package image.exifweb.album.page;

import image.exifweb.appconfig.AppConfigService;
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
public class AlbumPageRepository {
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
			q = session.createQuery("SELECT new image.exifweb.album.page.AlbumPage(" +
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
			q = session.createQuery("SELECT new image.exifweb.album.page.AlbumPage(" +
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
}
