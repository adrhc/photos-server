package image.jpa2x.repositories.album;

import image.cdm.album.page.AlbumPage;
import image.jpa2x.helper.ImageHelper;
import image.jpa2x.repositories.ESortType;
import image.jpa2x.repositories.appconfig.AppConfigRepository;
import org.hibernate.jpa.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static image.jpa2x.repositories.album.AlbumRepository.NULL_ALBUM_ID;

@Transactional
public class AlbumQueryRepositoryExImpl implements AlbumQueryRepositoryEx {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private ImageHelper imageHelper;
	@Autowired
	private AppConfigRepository appConfigRepository;

	@Override
	public int countPages(String toSearch, boolean viewHidden, boolean viewOnlyPrintable, Integer albumId) {
		boolean emptyAlbumId = albumId == null || albumId.equals(NULL_ALBUM_ID);
		boolean hasSearch = StringUtils.hasText(toSearch);
		TypedQuery<Long> q;
		q = this.em.createQuery("SELECT count(i) FROM Image i " +
				(emptyAlbumId ? "WHERE i.deleted = false " :
						"JOIN i.album a WHERE a.id = :albumId AND i.deleted = false ") +
				VIEW_HIDDEN_SQL + VIEW_PRINTABLE_SQL +
				(hasSearch ? "AND i.name LIKE :toSearch" : ""), Long.class);
		if (StringUtils.hasText(toSearch)) {
			// searches case-sensitive for name!
			q.setParameter("toSearch", "%" + toSearch + "%");
		} else {
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
	public List<AlbumPage> getPage(int pageNr, ESortType sort, String toSearch,
			boolean viewHidden, boolean viewOnlyPrintable, Integer albumId) {
		List<AlbumPage> thumbs = this.getPageFromDbImpl(pageNr, sort,
				toSearch, viewHidden, viewOnlyPrintable, albumId);
		this.imageHelper.appendImageDimensions(thumbs);
		this.imageHelper.appendImagePaths(thumbs);
		return thumbs;
	}

	private List<AlbumPage> getPageFromDbImpl(int pageNr, ESortType sort, String toSearch,
			boolean viewHidden, boolean viewOnlyPrintable, Integer albumId) {
		boolean emptyAlbumId = albumId == null || albumId.equals(NULL_ALBUM_ID);
		boolean hasSearch = StringUtils.hasText(toSearch);
		TypedQuery<AlbumPage> q;
		q = this.em.createQuery("SELECT new image.cdm.album.page.AlbumPage(" +
				"i.id, i.name, i.flags.hidden, i.flags.personal, i.flags.ugly, i.flags.duplicate, " +
				"i.flags.printable, i.imageMetadata.exifData.imageHeight, " +
				"i.imageMetadata.exifData.imageWidth, i.rating, a.cover.id, " +
				"i.imageMetadata.thumbLastModified, i.imageMetadata.dateTime, " +
				"i.lastUpdate, a.name, a.lastUpdate) " +
//				"thumbPath(a.name, i.imageMetadata.thumbLastModified, i.name), " +
//				"imagePath(a.name, i.imageMetadata.thumbLastModified, i.name)) " +
				"FROM Image i JOIN i.album a WHERE i.deleted = false " +
				(emptyAlbumId ? "" : "AND a.id = :albumId ") +
				VIEW_HIDDEN_SQL + VIEW_PRINTABLE_SQL +
				(hasSearch ? "AND i.name LIKE :toSearch " : "") +
				// sometimes dateTimeOriginal is the same for many images
				// so I have to keep the order with some additional column
				"ORDER BY i.imageMetadata.exifData.dateTimeOriginal " + sort + ", i.id", AlbumPage.class);
		if (hasSearch) {
			// searches case-sensitive for name!
			q.setParameter("toSearch", "%" + toSearch + "%");
		} else {
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

	/**
	 * NOW: computing lastUpdate based an all images and all related albums
	 */
	@Override
	public Optional<Date> getPageLastUpdate(int pageNr, String toSearch,
			boolean viewHidden, boolean viewOnlyPrintable, Integer albumId) {
		boolean emptyAlbumId = albumId == null || albumId.equals(NULL_ALBUM_ID);
		boolean hasSearch = StringUtils.hasText(toSearch);
		TypedQuery<Date> q;
		q = this.em.createQuery("SELECT max(" +
				"CASE WHEN i.lastUpdate > a.lastUpdate THEN i.lastUpdate ELSE a.lastUpdate END" +
				") " +
//				"thumbPath(a.name, i.imageMetadata.thumbLastModified, i.name), " +
//				"imagePath(a.name, i.imageMetadata.thumbLastModified, i.name)) " +
				"FROM Image i JOIN i.album a WHERE i.deleted = false " +
				(emptyAlbumId ? "" : "AND a.id = :albumId ") +
				VIEW_HIDDEN_SQL + VIEW_PRINTABLE_SQL +
				(hasSearch ? "AND i.name LIKE :toSearch " : ""), Date.class);
		if (hasSearch) {
			// searches case-sensitive for name!
			q.setParameter("toSearch", "%" + toSearch + "%");
		} else {
			q.setHint(QueryHints.HINT_CACHEABLE, !viewHidden && !viewOnlyPrintable);
		}
		if (!emptyAlbumId) {
			q.setParameter("albumId", albumId);
		}
		q.setParameter("viewHidden", viewHidden);
		q.setParameter("viewOnlyPrintable", viewOnlyPrintable);
		return Optional.ofNullable(q.getSingleResult());
	}
}
