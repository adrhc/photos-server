package image.exifweb.album.importer;

import image.exifweb.album.AlbumRepository;
import image.exifweb.album.events.AlbumEventBuilder;
import image.exifweb.album.events.AlbumEventsEmitter;
import image.exifweb.album.events.EAlbumEventType;
import image.exifweb.appconfig.AppConfigService;
import image.exifweb.image.ImageRepository;
import image.exifweb.image.events.EImageEventType;
import image.exifweb.image.events.ImageEventBuilder;
import image.exifweb.image.events.ImageEventsEmitter;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;
import image.exifweb.util.MutableValueHolder;
import io.reactivex.disposables.Disposable;
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

import static image.exifweb.image.events.EImageEventType.DELETED;
import static image.exifweb.image.events.EImageEventType.MARKED_DELETED;
import static image.exifweb.util.io.FileUtils.changeToOppositeExtensionCase;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AlbumImporterService {
	private static final Logger logger = LoggerFactory.getLogger(AlbumImporterService.class);
	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private ExifExtractorService exifExtractorService;
	@Inject
	private AppConfigService appConfigService;
	@Inject
	private ImageRepository imageRepository;
	@Inject
	private AlbumRepository albumRepository;
	@Inject
	private AlbumEventsEmitter albumEventsEmitter;
	@Inject
	private ImageEventsEmitter imageEventsEmitter;

	private Predicate<File> IS_NEW_VALID_ALBUM = albumPath -> {
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
		Album album = albumRepository.getAlbumByName(albumPath.getName());
		if (album != null) {
			// albumPath este un album deja importat deci NU nou
			return false;
		}
		// album inexistent in DB deci nou
		return true;
	};

	@Inject
	private ThumbUtils thumbUtils;

	public void importAlbumByName(String albumName) {
		importAlbumByPath(new File(appConfigService.getLinuxAlbumPath(), albumName));
	}

	public void importAllFromAlbumsRoot() {
		logger.debug("BEGIN");
		importFromAlbumsRoot(null);
		logger.debug("END");
	}

	public void importNewAlbumsOnly() {
		importFromAlbumsRoot(IS_NEW_VALID_ALBUM);
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
		Arrays.sort(files, Collections.reverseOrder());
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
		Album album = albumRepository.getAlbumByName(path.getName());
		boolean isNewAlbum = album == null;
		if (isNewAlbum) {
			// album inexistent in DB deci nou
			if (noFiles) {
				// path este album nou dar nu are poze
				sw.stop();
				return;
			}
			// creem un nou album (dir aferent are poze)
			album = albumRepository.create(path.getName());
		}
		// when importing a new album existsAtLeast1ImageChange will
		// always be true because we are not importing empty albums
		MutableValueHolder<Boolean> existsAtLeast1ImageChange = MutableValueHolder.of(false);
		Disposable subscription = imageEventsEmitter
				.imageEventsByType(true, EnumSet.allOf(EImageEventType.class))
				.take(1L).subscribe(
						ie -> existsAtLeast1ImageChange.setValue(true),
						t -> {
							logger.error(t.getMessage(), t);
							logger.error("[allOf(EImageEventType)] existsAtLeast1ImageChange");
						});
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
			deleteNotFoundImages(imageNames, album);
		}
		// todo: make sure to dispose even when an exception occurs
		subscription.dispose();
		if (existsAtLeast1ImageChange.getValue()) {
			albumEventsEmitter.emit(AlbumEventBuilder
					.of(EAlbumEventType.ALBUM_IMPORTED)
					.album(album).build());
		}
		sw.stop();
		logger.debug("END album:\n{}\n{}", path.getAbsolutePath(), sw.shortSummary());
	}

	/**
	 * @param imgFile
	 * @param album
	 * @return true = file still exists, false = file no longer exists
	 */
	private boolean importImageFromFile(File imgFile, Album album) {
		assert !imgFile.isDirectory() : "Wrong image file (is a directory):\n{}" + imgFile.getPath();
		Image dbImage = imageRepository.getImageByNameAndAlbumId(imgFile.getName(), album.getId());
		if (dbImage == null) {
			// not found in DB? then add it
			Image newImg = exifExtractorService.extractExif(imgFile);
			if (newImg == null) {
				logger.info("{} no longer exists!", imgFile.getPath());
				return false;
			}
			logger.debug("insert {}/{}", album.getName(), newImg.getName());
			newImg.setAlbum(album);
			imageRepository.persistImage(newImg);
			imageEventsEmitter.emit(ImageEventBuilder
					.of(EImageEventType.CREATED)
					.image(newImg).build());
		} else if (imgFile.lastModified() > dbImage.getDateTime().getTime()) {
			// check lastModified for image then extract EXIF and update
			logger.debug("update EXIF for {}/{}", album.getName(), dbImage.getName());
			Image imgWithUpdatedEXIF = exifExtractorService.extractExif(imgFile);
			imgWithUpdatedEXIF.setId(dbImage.getId());
			imgWithUpdatedEXIF.setAlbum(album);
			imageRepository.updateExif(imgWithUpdatedEXIF);
			imageEventsEmitter.emit(ImageEventBuilder
					.of(EImageEventType.EXIF_UPDATED)
					.image(imgWithUpdatedEXIF).build());
		} else {
			Date thumbLastModified = thumbUtils.getThumbLastModified(imgFile, dbImage.getDateTime());
			if (thumbLastModified.after(dbImage.getThumbLastModified())) {
				// check lastModified for thumb then update in DB lastModified date only
				logger.debug("update thumb's lastModified for {}/{}",
						album.getName(), dbImage.getName());
				Image updatedDbImg = imageRepository.updateThumbLastModifiedForImg(
						thumbLastModified, dbImage.getId());
				imageEventsEmitter.emit(ImageEventBuilder
						.of(EImageEventType.THUMB_LAST_MODIF_DATE_UPDATED)
						.image(updatedDbImg).build());
			}
		}
		return true;
	}

	/**
	 * Cached Album is detached so can't be used as persistent as required in this method.
	 *
	 * @param foundImageNames
	 */
	@Transactional
	public void deleteNotFoundImages(List<String> foundImageNames, Album album) {
		logger.debug("BEGIN {}", album.getName());
		Session session = sessionFactory.getCurrentSession();
		List<Image> images = imageRepository.getImagesByAlbumId(album.getId());
		images.forEach(image -> {
			String dbName = image.getName();
			int fsNameIdx = foundImageNames.indexOf(dbName);
			if (fsNameIdx >= 0) {
				// imagine existenta in DB cu acelas nume ca in file system
				return;
			}
			String oppositeExtensionCase = changeToOppositeExtensionCase(dbName);
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
				albumRepository.checkAndRemoveAlbumCover(image, album);
				session.delete(image);
				imageEventsEmitter.emit(imgEvBuilder.type(DELETED).build());
				return;
			}
			// status != 0 (adica e o imagine "prelucrata")
			logger.debug("poza din DB ({}) nu exista in file system: marchez ca stearsa", dbName);
			albumRepository.checkAndRemoveAlbumCover(image, album);
			image.setDeleted(true);
			imageEventsEmitter.emit(imgEvBuilder.type(MARKED_DELETED).build());
		});
		logger.debug("END {}", album.getName());
	}
}
