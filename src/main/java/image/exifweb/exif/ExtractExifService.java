package image.exifweb.exif;

import image.exifweb.album.AlbumService;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;
import image.exifweb.sys.AppConfigService;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ExtractExifService {
	private static final Logger logger = LoggerFactory.getLogger(ExtractExifService.class);
	@Inject
	private AppConfigService appConfigService;
	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private ImageExif imageExif;
	@Inject
	private AlbumInfo albumInfo;
	@Inject
	private AlbumService albumService;

	/**
	 * This is not @Async because we need the result from importedAlbums.
	 *
	 * @param importedAlbums
	 */
	public void importNewAlbumsOnly(List<Album> importedAlbums) {
		try {
			extractExif(new File(appConfigService.getLinuxAlbumPath()),
					null, true, importedAlbums);
			albumService.writeJsonForAllAlbums();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Async
	public void importAllFromAlbumsRoot() {
		try {
			extractExif(new File(appConfigService.getLinuxAlbumPath()),
					null, false, null);
			albumService.writeJsonForAllAlbums();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Async
	public void importAlbumByName(String albumName) {
		try {
			extractExif(new File(appConfigService.getLinuxAlbumPath() +
					File.separatorChar + albumName), null, false, null);
			albumService.writeJsonForAlbum(albumName);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * @param path
	 * @param album
	 * @return true = EXIF processed, false = file no longer exists
	 */
	public boolean extractExifFromFile(File path, Album album) {
		Image image = imageExif.extractExif(path);
		if (image == null) {
			logger.info("{} no longer exists!", path.getPath());
			return false;
		}
		image.setAlbum(album);
		saveImageExif(image);
		return true;
	}

	/**
	 * Exista posibilitatea ca in cadrul extragerii EXIF anumite
	 * albume sa fie sterse pt ca nu mai au poze in folderul aferent.
	 * De aceea avem evict pe lastUpdatedForAlbums.
	 *
	 * @param path
	 * @param album               is the path's album when path is a image-file otherwise is null
	 * @param onlyImportNewAlbums
	 * @param processedAlbums
	 */
	@CacheEvict(value = "default", key = "'lastUpdatedForAlbums'")
	public void extractExif(File path, Album album,
	                        boolean onlyImportNewAlbums,
	                        List<Album> processedAlbums) {
		// cazul in care path este o poza
		if (path.isFile()) {
			extractExifFromFile(path, album);
			return;
		}
		File[] files = path.listFiles();
		boolean noFiles = files == null || files.length == 0;
		boolean curDirIsAlbum = albumInfo.isAlbum(path.getName());
		if (curDirIsAlbum) {
			// path este un album
			if (onlyImportNewAlbums && noFiles) {
				// ne dorim sa fie album nou dar path nu are poze asa ca daca ar
				// fi intr-adevar album nou atunci nu ar avea sens sa-l import
				logger.warn("{} este gol!", path.getPath());
				return;
			}
			album = albumService.getAlbumByName(path.getName());
			if (album == null) {
				// album inexistent in DB
				if (noFiles) {
					// path este album nou dar nu are poze
					return;
				}
				// creem un nou album (dir aferent are poze)
				album = albumService.create(path.getName());
			} else if (onlyImportNewAlbums) {
				// cazul in care doar importam albume iar path este un album deja importat
				return;
			}
			if (processedAlbums != null) {
				// marcam albumul ca procesat
				processedAlbums.add(album);
			}
		} else if (noFiles) {
			// path este chiar root-ul albumelor si e gol
			logger.error("{} este gol!", path.getPath());
			return;
		}
		StopWatch sw = new StopWatch();
		sw.start(path.getAbsolutePath());
		// 1 level only album supported
		List<String> imageNames = curDirIsAlbum ? new ArrayList<>(noFiles ? 0 : files.length) : null;
		if (noFiles) {
			logger.debug("BEGIN album {}, 0 poze", path.getAbsolutePath());
		} else {
			logger.debug("BEGIN album {}, {} poze", path.getAbsolutePath(), files.length);
			for (File file : files) {
				if (curDirIsAlbum) {
					// 1 level only album supported
					if (extractExifFromFile(file, album)) {
						imageNames.add(file.getName());
					}
				} else {
					// is albums root
					extractExif(file, album, onlyImportNewAlbums, processedAlbums);
				}
			}
		}
		if (curDirIsAlbum && !onlyImportNewAlbums) {
			deleteNotFoundImages(imageNames, album);
		}
		sw.stop();
		logger.debug("END album " + path.getAbsolutePath() + ":\n" + sw.shortSummary());
	}

	@Transactional
	private void deleteNotFoundImages(List<String> foundImageNames, Album album) {
		logger.debug("imageNames.size: {}, albumId = {}", foundImageNames.size(), album.getId());
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT id as id, name as name FROM Image WHERE album.id = :albumId");
		q.setInteger("albumId", album.getId());
		q.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		List<Map<String, Object>> imagesDB = q.list();
		logger.debug("images.size: {}", imagesDB.size());
		String dbName;
		boolean usingOppositeCase;
		int fsNameIdx, removedCount = 0;
		Image image;
		for (Map<String, Object> imageCols : imagesDB) {
			usingOppositeCase = false;
			dbName = imageCols.get("name").toString();
			fsNameIdx = foundImageNames.indexOf(dbName);
			// searching for opposite string-case of dbName
			if (fsNameIdx < 0) {
				fsNameIdx = foundImageNames.indexOf(toFileNameWithOppositeExtensionCase(dbName));
				usingOppositeCase = true;
			}
			if (fsNameIdx < 0) {
				// poza din DB nu mai exista in file system
				image = (Image) session.load(Image.class, (Integer) imageCols.get("id"));
				if (image.getStatus().equals(Image.DEFAULT_STATUS)) {
					// status = 0
					logger.debug("poza din DB nu exista in file system: {} -> s-a sters", dbName);
					session.delete(image);
					removedCount++;
				} else {
					// status != 0 (adica e o imagine "prelucrata")
					logger.debug("poza din DB nu exista in file system: {} -> s-a marcat ca stearsa", dbName);
					image.setDeleted(true);
				}
			} else if (usingOppositeCase) {
				// diferenta de CASE; update photo's name & path in DB
				logger.debug("poza din DB ({}) cu nume diferit in file system: {}", dbName, foundImageNames.get(fsNameIdx));
				image = (Image) session.load(Image.class, (Integer) imageCols.get("id"));
				image.setName(foundImageNames.get(fsNameIdx));
			} else {
				// imagine existenta in DB cu acelas nume ca in file system
				// acesta este cazul in care nu am folosit nimic din db
				continue;
			}
			session.flush();// trebuie dat ca altfel e totul anulat de catre clear
			session.clear();// just clearing memory
		}
		if (imagesDB.size() == removedCount) {
			session.buildLockRequest(LockOptions.NONE).lock(album);
			album.setDeleted(true);
			session.flush();// trebuie dat ca altfel e totul anulat de catre clear
			session.clear();// just clearing memory
		}
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

	/**
	 * Require compile time aspectj weaving!
	 *
	 * @param imgWithNewExif
	 */
	private void saveImageExif(Image imgWithNewExif) {
		ImageIdAndDates imageIdAndDates = getImageIdAndDates(
				imgWithNewExif.getName(), imgWithNewExif.getAlbum().getId());
		if (imageIdAndDates == null) {
			persistImage(imgWithNewExif);
		} else if (imageIdAndDates.dateTime.before(imgWithNewExif.getDateTime())) {
			updateExifPropertiesInDB(imgWithNewExif, imageIdAndDates.id);
		} else if (imageIdAndDates.thumbLastModified.before(imgWithNewExif.getThumbLastModified())) {
			updateThumbLastModifiedForImg(imgWithNewExif.getThumbLastModified(), imageIdAndDates.id);
		}
	}

	@Transactional
	private void persistImage(Image image) {
		sessionFactory.getCurrentSession().persist(image);
	}

	@Transactional
	private void updateExifPropertiesInDB(Image image, Integer imageId) {
		Image dbImage = (Image) sessionFactory.getCurrentSession().load(Image.class, imageId);
		imageExif.copyExifProperties(image, dbImage);
	}

	@Transactional
	private void updateThumbLastModifiedForImg(Date thumbLastModified, Integer imageId) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("UPDATE Image SET thumbLastModified = :thumbLastModified WHERE id = :imageId");
		q.setDate("thumbLastModified", thumbLastModified);
		q.setInteger("imageId", imageId);
		q.executeUpdate();
	}

	@Transactional
	private ImageIdAndDates getImageIdAndDates(String name, Integer albumId) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT new image.exifweb.exif.ExtractExifService$ImageIdAndDates" +
				"(i.id, i.dateTime, i.thumbLastModified) FROM Image i WHERE i.name = :name AND i.album.id = :albumId");
		q.setString("name", name);
		q.setInteger("albumId", albumId);
		return (ImageIdAndDates) q.uniqueResult();
	}

	private static class ImageIdAndDates {
		private Integer id;
		private Date dateTime;
		private Date thumbLastModified;

		public ImageIdAndDates(Integer id, Date dateTime, Date thumbLastModified) {
			this.id = id;
			this.dateTime = dateTime;
			this.thumbLastModified = thumbLastModified;
		}
	}
}
