package image.persistence.repositories;

import image.cdm.album.page.AlbumPage;
import image.persistence.repository.AlbumPageRepository;
import image.persistence.repository.ESortType;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class AlbumPageRepositoryImpl implements AlbumPageRepository {
	@PersistenceContext
	private EntityManager em;
	@Inject
	private AppConfigRepository appConfigRepository;

	@Override
	public int countPages(String toSearch, boolean viewHidden, boolean viewOnlyPrintable, Integer albumId) {
		boolean emptyAlbumId = albumId == null || albumId.equals(NULL_ALBUM_ID);
		TypedQuery<Long> q;
		if (StringUtils.hasText(toSearch)) {
			q = this.em.createQuery("SELECT count(i) FROM Image i " +
					(emptyAlbumId ? "WHERE i.deleted = false " :
							"JOIN i.album a WHERE a.id = :albumId AND i.deleted = false ") +
					VIEW_HIDDEN_SQL + VIEW_PRINTABLE_SQL +
					"AND i.name LIKE :toSearch", Long.class);
			// searches case-sensitive for name!
			q.setParameter("toSearch", "%" + toSearch + "%");
		} else {
			q = this.em.createQuery("SELECT count(i) FROM Image i " +
					(emptyAlbumId ? "WHERE i.deleted = false " :
							"JOIN i.album a WHERE a.id = :albumId AND i.deleted = false ") +
					VIEW_HIDDEN_SQL + VIEW_PRINTABLE_SQL, Long.class);
			q.setHint(QueryHints.HINT_CACHEABLE, !viewHidden && !viewOnlyPrintable);
		}
		if (!emptyAlbumId) {
			q.setParameter("albumId", albumId);
		}
		q.setParameter("viewHidden", viewHidden);
		q.setParameter("viewOnlyPrintable", viewOnlyPrintable);
		return Double.valueOf(Math.ceil((q.getSingleResult()).doubleValue() /
				this.appConfigRepository.getPhotosPerPage())).intValue();
	}

	@Override
	public List<AlbumPage> getPageFromDb(int pageNr, ESortType sort, String toSearch,
	                                     boolean viewHidden, boolean viewOnlyPrintable, Integer albumId) {
		boolean emptyAlbumId = albumId == null || albumId.equals(NULL_ALBUM_ID);
		TypedQuery<AlbumPage> q;
		if (StringUtils.hasText(toSearch)) {
			q = this.em.createQuery("SELECT new image.cdm.album.page.AlbumPage(" +
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
					"ORDER BY i.imageMetadata.exifData.dateTimeOriginal " + sort, AlbumPage.class);
			// searches case-sensitive for name!
			q.setParameter("toSearch", "%" + toSearch + "%");
		} else {
			q = this.em.createQuery("SELECT new image.cdm.album.page.AlbumPage(" +
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
					"ORDER BY i.imageMetadata.exifData.dateTimeOriginal " + sort, AlbumPage.class);
			q.setHint(QueryHints.HINT_CACHEABLE, !viewHidden && !viewOnlyPrintable);
		}
		if (!emptyAlbumId) {
			q.setParameter("albumId", albumId);
		}
		q.setParameter("viewHidden", viewHidden);
		q.setParameter("viewOnlyPrintable", viewOnlyPrintable);
		q.setFirstResult((pageNr - 1) * this.appConfigRepository.getPhotosPerPage());
		q.setMaxResults(this.appConfigRepository.getPhotosPerPage());
		return q.getResultList();
	}
}
