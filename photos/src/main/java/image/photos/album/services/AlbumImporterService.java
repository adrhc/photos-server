package image.photos.album.services;

import image.cdm.image.ImageRating;
import image.cdm.image.status.ImageFlagEnum;
import image.infrastructure.messaging.album.AlbumEvent;
import image.infrastructure.messaging.album.AlbumTopic;
import image.infrastructure.messaging.image.ImageEvent;
import image.infrastructure.messaging.image.ImageEventTypeEnum;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneak;
import static image.infrastructure.messaging.album.AlbumEventTypeEnum.*;
import static image.jpa2x.util.AlbumUtils.albumNameFrom;
import static image.jpa2x.util.PathUtils.fileName;

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
	public List<Optional<AlbumEvent>> importAll() throws IOException {
		return this.importFilteredFromRoot(this.albumPathChecks::isValidAlbumPath);
	}

	/**
	 * import new albums only
	 */
	public List<Optional<AlbumEvent>> importNewAlbums() throws IOException {
		return this.importFilteredFromRoot(this.albumPathChecks::isValidNewAlbumPath);
	}

	/**
	 * import new album or rescan existing
	 */
	public Optional<AlbumEvent> importByAlbumName(String albumName) {
		Path path = this.albumHelper.absolutePathOf(albumName);
		if (!this.albumPathChecks.isValidAlbumPath(path)) {
			return Optional.of(AlbumEvent.of(new Album(albumName), FAILED_UPDATE));
		}
		return this.safelyImportByAlbumPath(path);
	}

	/**
	 * Filters album paths to be imported.
	 */
	private List<Optional<AlbumEvent>> importFilteredFromRoot(Predicate<Path> albumsFilter) throws IOException {
		List<Optional<AlbumEvent>> albumEvents = new ArrayList<>();
		Path root = this.albumHelper.albumsRoot();
		this.fileStoreService.walk1thLevel(root)
				.filter(albumsFilter)
				.sorted(Collections.reverseOrder())
				.forEach(path -> albumEvents.add(this.safelyImportByAlbumPath(path)));
		return albumEvents;
	}

	private Optional<AlbumEvent> safelyImportByAlbumPath(Path path) {
		try {
			return this.importByAlbumPath(path);
		} catch (IOException e) {
			return Optional.of(AlbumEvent
					.of(new Album(albumNameFrom(path)), FAILED_UPDATE));
		}
	}

	/**
	 * By now we already checked that path is a valid album path.
	 *
	 * @return true means "path/album created"
	 */
	private Optional<AlbumEvent> importByAlbumPath(Path path) throws IOException {
		String albumName = albumNameFrom(path);

		StopWatch stopWatch = new StopWatch(albumName);
		stopWatch.start(albumName);


		// determine or create album
		// path este album nou dar nu are poze
		Optional<AlbumEvent> albumEvent = this.findOrCreateAlbum(albumName);

		if (albumEvent.isEmpty()) {
			// new but empty album
			stopWatch.stop();
			log.debug("END album\n{}", stopWatch.shortSummary());
			return Optional.of(AlbumEvent.of(new Album(albumName), NEW_BUT_EMPTY));
		}

		Album album = albumEvent.get().getEntity();

		// When importing a new album existsAtLeast1ImageChange will
		// always be true because we are skipping (new) empty albums.
		AtomicBoolean isAtLeast1ImageChanged = new AtomicBoolean(false);

		Optional<List<String>> foundImageNames;

		// iterate and process image files
		if (this.albumHelper.isAlbumWithNoFiles(path)) {
			// existing empty album
			log.debug("BEGIN album with no pictures:\n{}", path);
			foundImageNames = Optional.empty();
		} else {
			// take only files existing in the album's directory but not sub-directories
			log.debug("BEGIN album has pictures:\n{}", path);

			int cpus = Runtime.getRuntime().availableProcessors();

			// heavy / light lists construction
			foundImageNames = Flux.fromStream(this.fileStoreService.walk(path))

					// Prepare this Flux by dividing data on a number of 'rails' matching the number of CPU cores, in a round-robin fashion.
					.parallel(cpus, cpus)
					// Specifies where each 'rail' will observe its incoming values with possibly work-stealing and a given prefetch amount.
					.runOn(Schedulers.newBoundedElastic(cpus, Integer.MAX_VALUE, "import"), 1)

					.log()
					.doOnNext(it -> log.debug("[before categorized processing] {}", fileName(it)))
					.map(imgFile -> this.imageImporterService.importFromFile(imgFile, album))
					.filter(Optional::isPresent)
					.map(Optional::get)

					.log()
					.doOnNext(it -> log.debug("[{} before db insert/update]", it.getType()))
					.map(it -> sneak(() -> it.getUnsafe().get()))

					.doOnNext(event -> isAtLeast1ImageChanged.compareAndSet(
							false, !event.getType().equals(ImageEventTypeEnum.NOTHING)))
					.map(event -> event.getEntity().getName())
					.collectSortedList(Comparator.naturalOrder())
					.blockOptional();
		}

		boolean isNewAlbum = albumEvent.get().getType().equals(CREATED);

		// remove db-images having no corresponding file
		if (!isNewAlbum) {
			foundImageNames.ifPresent(list -> {
				List<ImageEvent> events = this.removeImagesHavingNoFile(album, list);
				isAtLeast1ImageChanged.compareAndSet(false, !events.isEmpty());
			});
		}

		// mark album as dirty
		//
		// Better would be to only use isAtLeast1ImageChanged() because between
		// creation moment and here someone could clear the dirty flag!
		if (!isNewAlbum && isAtLeast1ImageChanged.get()) {
			this.albumRepository.markAsDirty(album.getId());
		}

		// album event emission (see AlbumExporterSubscription)
		if (isAtLeast1ImageChanged.get()) {
			this.albumTopic.emit(albumEvent.get());
		}

		stopWatch.stop();
		log.debug("END album\n{}", stopWatch.shortSummary());

		return albumEvent;
	}

	private Optional<AlbumEvent> findOrCreateAlbum(String albumName) {
		Album album = this.albumRepository.findByName(albumName);
		if (album != null) {
			// already existing album
			return Optional.of(AlbumEvent.of(album, UPDATED));
		}
		if (this.albumHelper.isAlbumWithNoFiles(this.albumHelper.absolutePathOf(albumName))) {
			// new empty album
			return Optional.empty();
		}
		// create new album (contains pictures)
		album = new Album(albumName);
		this.albumRepository.persist(album);
		return Optional.of(AlbumEvent.of(album, CREATED));
	}

	/**
	 * Album is detached so can't be used as persistent (as needed by this method).
	 */
	private List<ImageEvent> removeImagesHavingNoFile(
			Album album, List<String> foundImageFileNames) {
		log.debug("BEGIN {}", album.getName());
		// loading images from DB
		List<Image> images = this.imageQueryRepository.findByAlbumId(album.getId());
		List<ImageEvent> events = new ArrayList<>();
		// iterating images
		images.forEach(image -> {
			String dbName = image.getName();
			int fsNameIdx = foundImageFileNames.indexOf(dbName);
			if (fsNameIdx >= 0) {
				// db-image exists with name the same as image-fileName
				return;
			}
			String oppositeExtensionCase = this.imageHelper.changeToOppositeExtensionCase(dbName);
			// finding with the opposite extension case
			fsNameIdx = foundImageFileNames.indexOf(oppositeExtensionCase);
			if (fsNameIdx >= 0) {
				// found: update db-image name
				log.debug("poza din DB ({}) cu nume diferit in file system ({}):\nactualizez in DB cu {}",
						dbName, oppositeExtensionCase, oppositeExtensionCase);
				events.add(this.imageCUDService.changeName(oppositeExtensionCase, image.getId()));
			} else if (this.areEquals(image.getFlags(), ImageFlagEnum.DEFAULT) ||
					image.getRating() != ImageRating.MIN_RATING) {
				// not found (flags & rating not changed): purge image from DB
				log.debug("poza din DB ({}) nu exista in file system: sterg din DB", dbName);
				events.add(this.imageCUDService.safelyDeleteImage(image.getId()));
			} else {
				// not found (flags or rating changed): logically delete image
				log.debug("poza din DB ({}) nu exista in file system: marchez ca stearsa", dbName);
				Optional<ImageEvent> imageEvent = this.imageCUDService.markDeleted(image.getId());
				imageEvent.ifPresent(events::add);
			}
		});
		log.debug("END {}", album.getName());
		return events;
	}
}
