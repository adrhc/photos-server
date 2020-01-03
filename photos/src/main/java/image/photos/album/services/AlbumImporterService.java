package image.photos.album.services;

import image.cdm.image.status.EImageStatus;
import image.infrastructure.messaging.album.AlbumEvent;
import image.infrastructure.messaging.album.AlbumEventTypeEnum;
import image.infrastructure.messaging.album.AlbumTopic;
import image.infrastructure.messaging.image.ImageEventTypeEnum;
import image.infrastructure.messaging.image.ImageTopic;
import image.infrastructure.messaging.image.registration.FilteredTypesImageSubscription;
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
import reactor.core.Disposable;

import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

import static image.infrastructure.messaging.album.AlbumEventTypeEnum.CREATED;
import static image.photos.album.helpers.AlbumHelper.albumNameFrom;

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
	private final ImageTopic imageTopic;
	private final AlbumPathChecks albumPathChecks;
	private final AlbumHelper albumHelper;
	private final FileStoreService fileStoreService;

	public AlbumImporterService(ImageHelper imageHelper, ImageImporterService imageImporterService, ImageCUDService imageCUDService, ImageQueryRepository imageQueryRepository, AlbumRepository albumRepository, AlbumTopic albumTopic, ImageTopic imageTopic, AlbumPathChecks albumPathChecks, AlbumHelper albumHelper, FileStoreService fileStoreService) {
		this.imageHelper = imageHelper;
		this.imageImporterService = imageImporterService;
		this.imageQueryRepository = imageQueryRepository;
		this.imageCUDService = imageCUDService;
		this.albumRepository = albumRepository;
		this.albumTopic = albumTopic;
		this.imageTopic = imageTopic;
		this.albumPathChecks = albumPathChecks;
		this.albumHelper = albumHelper;
		this.fileStoreService = fileStoreService;
	}

	/**
	 * import new albums and rescan existing
	 */
	public void importAll() {
		importFilteredFromRoot(this.albumPathChecks::isValidAlbumPath);
	}

	/**
	 * import new albums only
	 */
	public void importNewAlbums() {
		importFilteredFromRoot(this.albumPathChecks::isValidNewAlbumPath);
	}

	/**
	 * import new album or rescan existing
	 */
	public void importByAlbumName(String albumName) {
		Path path = this.albumHelper.absolutePathOf(albumName);
		if (!this.albumPathChecks.isValidAlbumPath(path)) {
			throw new UnsupportedOperationException("Wrong album path:\n" + path);
		}
		importByAlbumPath(path);
	}

	/**
	 * Filters album paths to be imported.
	 */
	private void importFilteredFromRoot(Predicate<Path> albumsFilter) {
		Path root = this.albumHelper.albumsRoot();
		this.fileStoreService.walk(root, FileVisitOption.FOLLOW_LINKS)
				.filter(albumsFilter)
				.sorted(Collections.reverseOrder())
				.forEach(this::importByAlbumPath);
	}

	/**
	 * By now we already checked that path is a valid album path.
	 */
	private void importByAlbumPath(Path path) {
		StopWatch sw = new StopWatch();
		sw.start(path.toString());

		// determine or create album
		// path este album nou dar nu are poze
		Optional<AlbumEvent> albumEvent = findOrCreate(albumNameFrom(path));
		if (albumEvent.isEmpty()) {
			// album nou dar gol
			sw.stop();
			return;
		}

		Album album = albumEvent.get().getEntity();
		boolean isNewAlbum = albumEvent.get().getType().equals(CREATED);

		// Preparing an imageEvents-listener used to
		// determine whether exists any image changes.
		// When importing a new album existsAtLeast1ImageChange will
		// always be true because we are skipping (new) empty albums.
		ValueHolder<Boolean> isAtLeast1ImageChanged = ValueHolder.of(false);
		// TODO: propagate stamp to importImageFromFile and deleteNotFoundImages
		String stamp = UUID.randomUUID().toString();
		Disposable disposable = this.imageTopic.register(
				new FilteredTypesImageSubscription(
						stamp, EnumSet.allOf(ImageEventTypeEnum.class),
						flux -> flux
								.take(1L)
								.subscribe(event -> isAtLeast1ImageChanged.setValue(true))
				));

		// iterate and process image files
		List<String> foundImages = new ArrayList<>();
		if (this.albumHelper.isAlbumWithNoFiles(path)) {
			// existing empty album
			log.debug("BEGIN album with no pictures:\n{}", path);
		} else {
			// take only files existing in the album's directory but not sub-directories
			log.debug("BEGIN album has pictures:\n{}", path);
			this.fileStoreService.walk(path, FileVisitOption.FOLLOW_LINKS)
					.forEach(file -> {
						if (this.imageImporterService.importImageFromFile(file, album)) {
							foundImages.add(file.getFileName().toString());
						}
					});
		}

		// remove db-images having no corresponding file
		if (!isNewAlbum) {
			deleteNotFoundImages(foundImages, album);
		}

		// todo: make sure to dispose even when an exception occurs
		disposable.dispose();

		// see AlbumExporterService.postConstruct
		if (isNewAlbum) {
			this.albumTopic.emit(albumEvent.get());
		} else if (isAtLeast1ImageChanged.getValue()) {
			this.albumTopic.emit(AlbumEvent.builder()
					.type(AlbumEventTypeEnum.UPDATED)
					.entity(album).build());
		}

		sw.stop();
		log.debug("END album:\n{}\n{}", path, sw.shortSummary());
	}

	private Optional<AlbumEvent> findOrCreate(String albumName) {
		Album album = this.albumRepository.findByName(albumName);
		if (album != null) {
			// already existing album
			return Optional.of(AlbumEvent.builder().entity(album).build());
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
	 * Cached Album is detached so can't be used as persistent as required in this method.
	 */
	private void deleteNotFoundImages(List<String> foundImageNames, Album album) {
		log.debug("BEGIN {}", album.getName());
		List<Image> images = this.imageQueryRepository.findByAlbumId(album.getId());
//		List<Image> images = this.imageService.getImages(album.getId());
		images.forEach(image -> {
			String dbName = image.getName();
			int fsNameIdx = foundImageNames.indexOf(dbName);
			if (fsNameIdx >= 0) {
				// db-image having same name as file-image
				return;
			}
			String oppositeExtensionCase = this.imageHelper.changeToOppositeExtensionCase(dbName);
			fsNameIdx = foundImageNames.indexOf(oppositeExtensionCase);
			if (fsNameIdx >= 0) {
				// changeName
				log.debug("poza din DB ({}) cu nume diferit in file system ({}):\nactualizez in DB cu {}",
						dbName, oppositeExtensionCase, oppositeExtensionCase);
				this.imageCUDService.changeName(oppositeExtensionCase, image.getId());
			} else if (areEquals(image.getFlags(), EImageStatus.DEFAULT)) {
				// status = 0
				log.debug("poza din DB ({}) nu exista in file system: sterg din DB", dbName);
				this.imageCUDService.safelyDeleteImage(image.getId());
			} else {
				// status != 0 (adica e o imagine "prelucrata")
				log.debug("poza din DB ({}) nu exista in file system: marchez ca stearsa", dbName);
				this.imageCUDService.markDeleted(image.getId());
			}
		});
		log.debug("END {}", album.getName());
	}
}
