package image.exifweb.album;

import image.exifweb.album.events.AlbumEventBuilder;
import image.exifweb.album.events.AlbumEventsEmitter;
import image.exifweb.album.events.EAlbumEventType;
import image.exifweb.exif.ImageExif;
import image.exifweb.image.ImageService;
import image.exifweb.image.events.EImageEventType;
import image.exifweb.image.events.ImageEventBuilder;
import image.exifweb.image.events.ImageEventsEmitter;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;
import image.exifweb.sys.AppConfigService;
import image.exifweb.util.ValueHolder;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AlbumImporter {
	private static final Logger logger = LoggerFactory.getLogger(AlbumImporter.class);
	@Inject
	private AppConfigService appConfigService;
	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private ImageExif imageExif;
	@Inject
	private ImageService imageService;
	@Inject
	private AlbumService albumService;
	@Inject
	private AlbumEventsEmitter albumEventsEmitter;
	@Inject
	private ImageEventsEmitter imageEventsEmitter;

	private Predicate<File> IS_NEW_ALBUM = albumPath -> {
		// cazul in care albumPath este o poza
		if (albumPath.isFile()) {
			logger.error("Wrong albumPath (is a file):\n{}", albumPath.getPath());
			return false;
		}
		// cazul in care albumPath este un album
		File[] albumFiles = albumPath.listFiles();
		boolean noFiles = albumFiles == null || albumFiles.length == 0;
		if (noFiles) {
			// ne dorim sa fie album nou dar albumPath nu are poze asa ca daca
			// ar fi intr-adevar album nou atunci nu ar avea sens sa-l import
			logger.warn("{} este gol!", albumPath.getPath());
			return false;
		}
		Album album = albumService.getAlbumByName(albumPath.getName());
		if (album != null) {
			// albumPath este un album deja importat deci NU nou
			return false;
		}
		// album inexistent in DB deci nou
		return true;
	};

	public void importAlbumByName(String albumName) {
		importAlbumByPath(new File(appConfigService.getLinuxAlbumPath(), albumName));
	}

	public void importAllFromAlbumsRoot() {
		logger.debug("BEGIN");
		importFromAlbumsRoot(null);
	}

	public void importNewAlbumsOnly() {
		importFromAlbumsRoot(IS_NEW_ALBUM);
	}

	/**
	 * Filters albums to be imported with albumsFilter.
	 *
	 * @param albumsFilter
	 */
	private void importFromAlbumsRoot(Predicate<File> albumsFilter) {
		File albumsRoot = new File(appConfigService.getLinuxAlbumPath());
		File[] files = albumsRoot.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		Arrays.sort(files);
		if (albumsFilter == null) {
			Stream.of(files)
					.forEach(this::importAlbumByPath);
		} else {
			Stream.of(files)
					.filter(albumsFilter)
					.forEach(this::importAlbumByPath);
		}
	}

	/**
	 * @param path
	 */
	private void importAlbumByPath(File path) {
		// cazul in care path este o poza
		if (path.isFile()) {
			logger.error("Wrong path (is a file):\n{}", path.getPath());
			throw new UnsupportedOperationException("Wrong path (is a file):\n" + path.getPath());
		}
		StopWatch sw = new StopWatch();
		sw.start(path.getAbsolutePath());
		// cazul in care path este un album
		File[] files = path.listFiles();
		boolean noFiles = files == null || files.length == 0;
		if (!noFiles) {
			Arrays.sort(files);
		}
		Album album = albumService.getAlbumByName(path.getName());
		boolean isNewAlbum = album == null;
		if (isNewAlbum) {
			// album inexistent in DB deci nou
			if (noFiles) {
				// path este album nou dar nu are poze
				sw.stop();
				return;
			}
			// creem un nou album (dir aferent are poze)
			album = albumService.create(path.getName());
		}
		// when importing a new album existsAtLeast1ImageChange will
		// always be true because we are not importing empty albums
		ValueHolder<Boolean> existsAtLeast1ImageChange = ValueHolder.of(false);
		imageEventsEmitter.imageEventsByType(true,
				EnumSet.allOf(EImageEventType.class)).take(1L)
				.subscribe(ie -> existsAtLeast1ImageChange.setValue(true));
		// at this point: album != null
		List<String> imageNames = new ArrayList<>(noFiles ? 0 : files.length);
		if (noFiles) {
			logger.debug("BEGIN album with 0 poze:\n{}", path.getAbsolutePath());
		} else {
			// 1 level only album supported
			logger.debug("BEGIN album with {} poze:\n{}", files.length, path.getAbsolutePath());
			for (File file : files) {
				if (importImageFromFile(file, album)) {
					imageNames.add(file.getName());
				}
			}
		}
		if (!isNewAlbum) {
			albumService.deleteNotFoundImages(imageNames, album);
		}
		if (existsAtLeast1ImageChange.getValue()) {
			albumEventsEmitter.emit(AlbumEventBuilder
					.of(EAlbumEventType.ALBUM_IMPORTED)
					.album(album).build());
		}
		sw.stop();
		logger.debug("END album:\n{}\n{}", path.getAbsolutePath(), sw.shortSummary());
	}

	/**
	 * @param path
	 * @param album
	 * @return true = EXIF processed, false = file no longer exists
	 */
	private boolean importImageFromFile(File path, Album album) {
		if (path.isDirectory()) {
			logger.error("Wrong path (is a directory):\n{}", path.getPath());
			throw new UnsupportedOperationException("Wrong path (is a directory):\n" + path.getPath());
		}
		Image image = imageExif.extractExif(path);
		if (image == null) {
			logger.info("{} no longer exists!", path.getPath());
			return false;
		}
		image.setAlbum(album);
		saveOrUpdateImage(image);
		return true;
	}

	/**
	 * Require compile time aspectj weaving!
	 *
	 * @param imgWithNewExif
	 */
	private void saveOrUpdateImage(Image imgWithNewExif) {
		ImageIdAndDates imageIdAndDates = getImageIdAndDates(
				imgWithNewExif.getName(), imgWithNewExif.getAlbum().getId());
		if (imageIdAndDates == null) {
			persistImage(imgWithNewExif);
			imageEventsEmitter.emit(ImageEventBuilder
					.of(EImageEventType.CREATED)
					.image(imgWithNewExif).build());
		} else if (imageIdAndDates.dateTime.before(imgWithNewExif.getDateTime())) {
			updateExifPropertiesInDB(imgWithNewExif, imageIdAndDates.id);
			imageEventsEmitter.emit(ImageEventBuilder
					.of(EImageEventType.EXIF_UPDATED)
					.image(imgWithNewExif).build());
		} else if (imageIdAndDates.thumbLastModified.before(imgWithNewExif.getThumbLastModified())) {
			imageService.updateThumbLastModifiedForImg(
					imgWithNewExif.getThumbLastModified(), imageIdAndDates.id);
			imageEventsEmitter.emit(ImageEventBuilder
					.of(EImageEventType.THUMB_UPDATED)
					.image(imgWithNewExif).build());
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

	@Transactional(readOnly = true)
	private ImageIdAndDates getImageIdAndDates(String name, Integer albumId) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery("SELECT new image.exifweb.album.AlbumImporter$ImageIdAndDates" +
				"(i.id, i.dateTime, i.thumbLastModified) FROM Image i " +
				"WHERE i.name = :name AND i.album.id = :albumId").setCacheable(true);
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
