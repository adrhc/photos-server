package image.photos.album.services;

import image.cdm.image.ImageRating;
import image.cdm.image.status.EImageStatus;
import image.infrastructure.messaging.album.AlbumEvent;
import image.infrastructure.messaging.album.AlbumTopic;
import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.ImageQueryRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.IImageFlagsUtils;
import image.photos.album.helpers.AlbumHelper;
import image.photos.album.helpers.AlbumPathChecks;
import image.photos.image.helpers.ImageHelper;
import image.photos.image.services.ImageImporterService;
import image.photos.infrastructure.database.ImageCUDService;
import image.photos.infrastructure.filestore.FileStoreService;
import image.photos.util.ValueHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.FileNotFoundException;
import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static image.infrastructure.messaging.album.AlbumEventTypeEnum.CREATED;
import static image.infrastructure.messaging.album.AlbumEventTypeEnum.UPDATED;
import static image.photos.album.helpers.AlbumHelper.albumNameFrom;
import static image.photos.infrastructure.filestore.PathUtils.fileName;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
@Slf4j
public class AlbumImporterService implements IImageFlagsUtils {
	private final ImageImporterService imageImporterService;
	private final ImageHelper imageHelper;
	private final ImageQueryRepository imageQueryRepository;
	private final ImageCUDService imageCUDService;
	private final AlbumRepository albumRepository;
	private final AlbumTopic albumTopic;
	private final AlbumPathChecks albumPathChecks;
	private final AlbumHelper albumHelper;
	private final FileStoreService fileStoreService;

	public AlbumImporterService(ImageHelper imageHelper, ImageImporterService imageImporterService, ImageCUDService imageCUDService, ImageQueryRepository imageQueryRepository, AlbumRepository albumRepository, AlbumTopic albumTopic, AlbumPathChecks albumPathChecks, AlbumHelper albumHelper, FileStoreService fileStoreService) {
		this.imageHelper = imageHelper;
		this.imageImporterService = imageImporterService;
		this.imageQueryRepository = imageQueryRepository;
		this.imageCUDService = imageCUDService;
		this.albumRepository = albumRepository;
		this.albumTopic = albumTopic;
		this.albumPathChecks = albumPathChecks;
		this.albumHelper = albumHelper;
		this.fileStoreService = fileStoreService;
	}

	/**
	 * import new albums and rescan existing
	 */
	public List<Optional<AlbumEvent>> importAll() {
		return importFilteredFromRoot(this.albumPathChecks::isValidAlbumPath);
	}

	/**
	 * import new albums only
	 */
	public List<Optional<AlbumEvent>> importNewAlbums() {
		return importFilteredFromRoot(this.albumPathChecks::isValidNewAlbumPath);
	}

	/**
	 * import new album or rescan existing
	 */
	public Optional<AlbumEvent> importByAlbumName(String albumName) {
		Path path = this.albumHelper.absolutePathOf(albumName);
		if (!this.albumPathChecks.isValidAlbumPath(path)) {
			throw new UnsupportedOperationException("Wrong album path:\n" + path);
		}
		return importByAlbumPath(path);
	}

	/**
	 * Filters album paths to be imported.
	 */
	private List<Optional<AlbumEvent>> importFilteredFromRoot(Predicate<Path> albumsFilter) {
		List<Optional<AlbumEvent>> albumEvents = new ArrayList<>();
		Path root = this.albumHelper.albumsRoot();
		this.fileStoreService.walk(root, FileVisitOption.FOLLOW_LINKS)
				.filter(albumsFilter)
				.sorted(Collections.reverseOrder())
				.forEach(path -> albumEvents.add(this.importByAlbumPath(path)));
		return albumEvents;
	}

	/**
	 * By now we already checked that path is a valid album path.
	 *
	 * @return true means "path/album created"
	 */
	private Optional<AlbumEvent> importByAlbumPath(Path path) {
		StopWatch sw = new StopWatch();
		sw.start(path.toString());

		// determine or create album
		// path este album nou dar nu are poze
		Optional<AlbumEvent> albumEvent = findOrCreate(albumNameFrom(path));

		if (albumEvent.isEmpty()) {
			// new but empty album
			sw.stop();
			return albumEvent;
		}

		Album album = albumEvent.get().getEntity();

		// When importing a new album existsAtLeast1ImageChange will
		// always be true because we are skipping (new) empty albums.
		ValueHolder<Boolean> isAtLeast1ImageChanged = ValueHolder.of(false);

		// iterate and process image files
		List<String> foundImageFileNames = new ArrayList<>();
		if (this.albumHelper.isAlbumWithNoFiles(path)) {
			// existing empty album
			log.debug("BEGIN album with no pictures:\n{}", path);
		} else {
			// take only files existing in the album's directory but not sub-directories
			log.debug("BEGIN album has pictures:\n{}", path);
			this.fileStoreService.walk(path, FileVisitOption.FOLLOW_LINKS)
					.forEach(imgFile -> {
						try {
							isAtLeast1ImageChanged.setValue(
									this.imageImporterService.importFromFile(imgFile, album));
							foundImageFileNames.add(fileName(imgFile));
						} catch (FileNotFoundException e) {
							log.error("{} no longer exists!", imgFile);
						}
					});
		}

		boolean isNewAlbum = albumEvent.get().getType().equals(UPDATED);

		if (!isNewAlbum) {
			// remove db-images having no corresponding file
			removeImagesHavingNoFile(album, foundImageFileNames,
					() -> isAtLeast1ImageChanged.setValue(true));
		}

		if (isNewAlbum || isAtLeast1ImageChanged.getValue()) {
			// album event emission (see AlbumExporterSubscription)
			this.albumTopic.emit(albumEvent.get());
		}

		sw.stop();
		log.debug("END album:\n{}\n{}", path, sw.shortSummary());

		return albumEvent;
	}

	private Optional<AlbumEvent> findOrCreate(String albumName) {
		Album album = this.albumRepository.findByName(albumName);
		if (album != null) {
			// already existing album
			return Optional.of(AlbumEvent.of(album, UPDATED));
		}
		if (this.albumHelper.isAlbumWithNoFiles(this.albumHelper.absolutePathOf(albumName))) {
			// new empty album
			return Optional.empty();
		}
		// creem un nou album (path aferent contine poze)
		album = this.albumRepository.createByName(albumName);
		return Optional.of(AlbumEvent.of(album, CREATED));
	}

	/**
	 * Album is detached so can't be used as persistent (as needed by this method).
	 */
	private void removeImagesHavingNoFile(Album album,
			List<String> foundImageFileNames, Runnable dbChangedCallBack) {
		log.debug("BEGIN {}", album.getName());
		List<Image> images = this.imageQueryRepository.findByAlbumId(album.getId());
		images.forEach(image -> {
			String dbName = image.getName();
			int fsNameIdx = foundImageFileNames.indexOf(dbName);
			if (fsNameIdx >= 0) {
				// db-image having same name as file-image
				return;
			}
			String oppositeExtensionCase = this.imageHelper.changeToOppositeExtensionCase(dbName);
			fsNameIdx = foundImageFileNames.indexOf(oppositeExtensionCase);
			if (fsNameIdx >= 0) {
				// change image's name
				log.debug("poza din DB ({}) cu nume diferit in file system ({}):\nactualizez in DB cu {}",
						dbName, oppositeExtensionCase, oppositeExtensionCase);
				this.imageCUDService.changeName(oppositeExtensionCase, image.getId());
			} else if (areEquals(image.getFlags(), EImageStatus.DEFAULT) ||
					image.getRating() != ImageRating.MIN_RATING) {
				// purge image from DB
				log.debug("poza din DB ({}) nu exista in file system: sterg din DB", dbName);
				this.imageCUDService.safelyDeleteImage(image.getId());
			} else {
				// logically delete image (status != 0 means a "reviewed" image)
				log.debug("poza din DB ({}) nu exista in file system: marchez ca stearsa", dbName);
				this.imageCUDService.markDeleted(image.getId());
			}
			dbChangedCallBack.run();
		});
		log.debug("END {}", album.getName());
	}
}
