package image.photos.album;

import image.cdm.image.status.EImageStatus;
import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.AppConfigRepository;
import image.jpa2x.repositories.ImageRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.IImageFlagsUtils;
import image.persistence.entity.image.ImageMetadata;
import image.photos.events.album.AlbumEventBuilder;
import image.photos.events.album.AlbumEventsEmitter;
import image.photos.events.album.EAlbumEventType;
import image.photos.events.image.EImageEventType;
import image.photos.events.image.ImageEventBuilder;
import image.photos.events.image.ImageEventsEmitter;
import image.photos.image.ExifExtractorService;
import image.photos.image.ImageService;
import image.photos.image.ImageUtils;
import image.photos.image.ThumbUtils;
import image.photos.util.MutableValueHolder;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static image.photos.events.image.EImageEventType.DELETED;
import static image.photos.events.image.EImageEventType.MARKED_DELETED;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class AlbumImporterService implements IImageFlagsUtils {
	private static final Logger logger = LoggerFactory.getLogger(AlbumImporterService.class);
	@Autowired
	private ImageUtils imageUtils;
	@Autowired
	private ExifExtractorService exifExtractorService;
	@Autowired
	private AppConfigRepository appConfigRepository;
	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private ImageService imageService;
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private AlbumEventsEmitter albumEventsEmitter;
	@Autowired
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
		Album album = this.albumRepository.findByName(albumPath.getName());
		if (album != null) {
			// albumPath este un album deja importat deci NU nou
			return false;
		}
		// album inexistent in DB deci nou
		return true;
	};

	@Autowired
	private ThumbUtils thumbUtils;

	public void importAlbumByName(String albumName) {
		importAlbumByPath(new File(this.appConfigRepository.getAlbumsPath(), albumName));
	}

	public void importAllFromAlbumsRoot() {
		logger.debug("BEGIN");
		importFromAlbumsRoot(null);
		logger.debug("END");
	}

	public void importNewAlbumsOnly() {
		importFromAlbumsRoot(this.IS_NEW_VALID_ALBUM);
	}

	/**
	 * Filters albums to be imported with albumsFilter.
	 *
	 * @param albumsFilter
	 */
	private void importFromAlbumsRoot(Predicate<File> albumsFilter) {
		File albumsRoot = new File(this.appConfigRepository.getAlbumsPath());
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
		Album album = this.albumRepository.findByName(path.getName());
		boolean isNewAlbum = album == null;
		if (isNewAlbum) {
			// album inexistent in DB deci nou
			if (noFiles) {
				// path este album nou dar nu are poze
				sw.stop();
				return;
			}
			// creem un nou album (dir aferent are poze)
			album = this.albumRepository.createByName(path.getName());
		}
		// when importing a new album existsAtLeast1ImageChange will
		// always be true because we are not importing empty albums
		MutableValueHolder<Boolean> existsAtLeast1ImageChange = MutableValueHolder.of(false);
		Disposable subscription = this.imageEventsEmitter
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
			this.albumEventsEmitter.emit(AlbumEventBuilder
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
//		Image dbImage = this.imageRepository.findByNameAndAlbumId(imgFile.getName(), album.getId());
		Image dbImage = this.imageService.findByNameAndAlbumId(imgFile.getName(), album.getId());
		if (dbImage == null) {
			// not found in DB? then add it
			return createImageFromFile(imgFile, album);
		}
		if (imgFile.lastModified() > dbImage.getImageMetadata().getDateTime().getTime()) {
			// check lastModified for image then extract EXIF and update
			updateImageMetadataFromFile(imgFile, dbImage);
		} else {
			Date thumbLastModified = this.thumbUtils
					.getThumbLastModified(imgFile, dbImage.getImageMetadata().getDateTime());
			if (thumbLastModified.after(dbImage.getImageMetadata().getThumbLastModified())) {
				// check lastModified for thumb then update in DB lastModified date only
				updateThumbLastModifiedForImgFile(thumbLastModified, dbImage.getId());
			}
		}
		return true;
	}

	private void updateThumbLastModifiedForImgFile(Date thumbLastModified, Integer imageId) {
		Image updatedDbImg = this.imageRepository
				.updateThumbLastModifiedForImg(thumbLastModified, imageId);
		logger.debug("updated thumb's lastModified for {}", updatedDbImg.getName());
		this.imageEventsEmitter.emit(ImageEventBuilder
				.of(EImageEventType.THUMB_LAST_MODIF_DATE_UPDATED)
				.image(updatedDbImg).build());
	}

	private void updateImageMetadataFromFile(File imgFile, Image dbImage) {
		logger.debug("update EXIF for {}/{}",
				imgFile.getParentFile().getName(), dbImage.getName());
		ImageMetadata imageMetadata = this.exifExtractorService.extractMetadata(imgFile);
		Image imgWithUpdatedMetadata = this.imageRepository
				.updateImageMetadata(imageMetadata, dbImage.getId());
		this.imageEventsEmitter.emit(ImageEventBuilder
				.of(EImageEventType.EXIF_UPDATED)
				.image(imgWithUpdatedMetadata).build());
	}

	private boolean createImageFromFile(File imgFile, Album album) {
		ImageMetadata imageMetadata = this.exifExtractorService.extractMetadata(imgFile);
		if (imageMetadata == null) {
			logger.info("{} no longer exists!", imgFile.getPath());
			return false;
		}
		if (this.imageUtils.imageExistsInOtherAlbum(imgFile,
				imageMetadata.getExifData().getDateTimeOriginal(), album.getId())) {
			logger.debug("Image {}\tto insert into album {} already exists in another album!",
					imgFile.getName(), album.getName());
			return false;
		}
		logger.debug("insert {}/{}", album.getName(), imgFile.getName());
		Image newImg = new Image();
		newImg.setImageMetadata(imageMetadata);
		newImg.setName(imgFile.getName());
		newImg.setAlbum(album);
		this.imageRepository.persist(newImg);
		this.imageEventsEmitter.emit(ImageEventBuilder
				.of(EImageEventType.CREATED)
				.image(newImg).build());
		return true;
	}

	/**
	 * Cached Album is detached so can't be used as persistent as required in this method.
	 *
	 * @param foundImageNames
	 */
	private void deleteNotFoundImages(List<String> foundImageNames, Album album) {
		logger.debug("BEGIN {}", album.getName());
		List<Image> images = this.imageRepository.findByAlbumId(album.getId());
//		List<Image> images = this.albumService.getImages(album.getId());
		images.forEach(image -> {
			String dbName = image.getName();
			int fsNameIdx = foundImageNames.indexOf(dbName);
			if (fsNameIdx >= 0) {
				// imagine existenta in DB cu acelas nume ca in file system
				return;
			}
			String oppositeExtensionCase = this.imageUtils.changeToOppositeExtensionCase(dbName);
			fsNameIdx = foundImageNames.indexOf(oppositeExtensionCase);
			ImageEventBuilder imgEvBuilder = new ImageEventBuilder().album(album).image(image);
			if (fsNameIdx >= 0) {
				logger.debug("poza din DB ({}) cu nume diferit in file system ({}):\nactualizez in DB cu {}",
						dbName, oppositeExtensionCase, oppositeExtensionCase);
				this.imageRepository.changeName(oppositeExtensionCase, image.getId());
				this.imageEventsEmitter.emit(imgEvBuilder.type(EImageEventType.UPDATED).build());
				return;
			}
			if (areEquals(image.getFlags(), EImageStatus.DEFAULT)) {
				// status = 0
				logger.debug("poza din DB ({}) nu exista in file system: sterg din DB", dbName);
				this.imageRepository.safelyDeleteImage(image.getId());
				this.imageEventsEmitter.emit(imgEvBuilder.type(DELETED).build());
				return;
			}
			// status != 0 (adica e o imagine "prelucrata")
			logger.debug("poza din DB ({}) nu exista in file system: marchez ca stearsa", dbName);
			if (this.imageRepository.markDeleted(image.getId())) {
				this.imageEventsEmitter.emit(imgEvBuilder.type(MARKED_DELETED).build());
			}
		});
		logger.debug("END {}", album.getName());
	}
}
