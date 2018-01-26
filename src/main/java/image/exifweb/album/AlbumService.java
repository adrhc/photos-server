package image.exifweb.album;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.exifweb.exif.ExtractExifService;
import image.exifweb.image.ImageDimensions;
import image.exifweb.image.ImageThumb;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;
import image.exifweb.persistence.view.AlbumCover;
import image.exifweb.sys.AppConfigService;
import org.apache.commons.lang.text.StrBuilder;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/3/14
 * Time: 10:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AlbumService {
	public static final String ALBUMS_PAGE_JSON = "albums.json";
	public static final String PAGE_COUNT = "pageCount";
	public static final String PHOTOS_PER_PAGE = "photosPerPage";
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
	private ObjectMapper json;
	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private AppConfigService appConfigService;
	@Inject
	private ExtractExifService extractExifService;

	public Album create(String name) {
		Album album = new Album(name);
		sessionFactory.getCurrentSession().persist(album);
		return album;
	}

	@Transactional
	public Album getAlbumById(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		return (Album) session.get(Album.class, id);// get initializeaza entity
	}

	@Transactional
	public Album getAlbumByName(String name) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("FROM Album WHERE name = :name");
		return (Album) q.setString("name", name).uniqueResult();
	}

	@Transactional
	@Cacheable(value = "default", key = "'lastUpdatedForAlbums'")
	public Date getLastUpdatedForAlbums() {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT max(lastUpdate) FROM Album");
		return (Date) q.uniqueResult();
	}

	public List<AlbumCover> getAllCovers(boolean computeDimensionForThumbs) {
		List<AlbumCover> covers = getAllCovers();
		if (computeDimensionForThumbs) {
			prepareImageDimensions(covers);
		}
		prepareURI(covers);
		return covers;
	}

	@Transactional
	public List<AlbumCover> getAllCovers() {
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

	public void writeJsonForAlbum(String name) throws IOException {
		Album album = getAlbumByName(name);
		if (album == null) {
			return;
		}
		writeJsonForAlbum(album);
	}

	public void writeJsonForAlbum(Album album) throws IOException {
		logger.debug("BEGIN {}", album.getName());
		if (album.isDeleted()) {
			logger.debug("END (is deleted) {}", album.getName());
			return;
		}
		int pageCount = getPageCount(null, false, album.getId());
		int photosPerPage = appConfigService.getPhotosPerPage();
		Map<String, Object> map = new HashMap<>();
		map.put(PAGE_COUNT, pageCount);
		map.put(PHOTOS_PER_PAGE, photosPerPage);
		File dir = new File(appConfigService.getConfig("photos json FS path") +
				File.separatorChar + album.getId());
		dir.mkdirs();
		File file = new File(dir, "pageCount.json");
		// write pageCount info
		json.writeValue(file, map);
		for (int i = 0; i < pageCount; i++) {
			// write page i + 1 asc
			json.writeValue(new File(dir, "asc" + String.valueOf(i + 1) + ".json"),
					getPage(i + 1, "asc", null, false, album.getId()));
			// write page i + 1 desc
			json.writeValue(new File(dir, "desc" + String.valueOf(i + 1) + ".json"),
					getPage(i + 1, "desc", null, false, album.getId()));
		}
		clearDirtyForAlbum(album.getId());
		logger.debug("END {}", album.getName());
	}

	@Transactional
	@CacheEvict(value = "default", key = "'lastUpdatedForAlbums'")
	public void putAlbumCover(Integer imageId) throws IOException {
		Session session = sessionFactory.getCurrentSession();
		Image image = (Image) session.load(Image.class, imageId);
		Album album = image.getAlbum();
		album.setCover(image);
		album.setDirty(true);
	}

	/**
	 * @param albumId
	 * @return
	 * @throws IOException
	 */
	@Transactional
	@CacheEvict(value = "default", key = "'lastUpdatedForAlbums'")
	public void clearDirtyForAlbum(Integer albumId) throws IOException {
		Session session = sessionFactory.getCurrentSession();
		Album album = (Album) session.load(Album.class, albumId);
		album.setDirty(false);
	}

	public void writeJsonForAllAlbums() throws IOException {
		List<AlbumCover> albumCovers = getAllCovers();
		for (AlbumCover albumCover : albumCovers) {
			writeJsonForAlbum(new Album(albumCover));
		}
	}

	/**
	 * Necesara doar la debug din js/grunt fara serverul java.
	 *
	 * @throws IOException
	 */
	public void writeJsonForAlbumsPage() throws IOException {
		File file = new File(appConfigService.getConfig("photos json FS path") +
				File.separatorChar + ALBUMS_PAGE_JSON);
		file.getParentFile().mkdirs();
		List<AlbumCover> albums = getAllCovers(true);
		json.writeValue(file, albums);
	}

	@CacheEvict(value = "default", key = "'lastUpdatedForAlbums'")
	public String importAlbums() throws IOException {
		List<Album> importedAlbums = new ArrayList<>();
		extractExifService.extractExif(new File(
				appConfigService.getLinuxAlbumPath()), null, true, importedAlbums);
		if (importedAlbums.isEmpty()) {
			return null;
		}
		for (Album album : importedAlbums) {
			writeJsonForAlbum(album);
		}
		return importedAlbumsToString(importedAlbums);
	}

	private String importedAlbumsToString(List<Album> importedAlbums) {
		StrBuilder sb = new StrBuilder();
		for (Album album : importedAlbums) {
			sb.append(album.getName()).append(", ");
		}
		sb.delete(sb.length() - ", ".length(), sb.length());
		return sb.toString();
	}
}