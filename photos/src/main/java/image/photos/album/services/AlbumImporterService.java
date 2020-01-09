package image.photos.album.services;

import image.cdm.image.ImageRating;
import image.cdm.image.status.ImageFlagEnum;
import image.infrastructure.messaging.album.AlbumEvent;
import image.infrastructure.messaging.album.AlbumTopic;
import image.infrastructure.messaging.image.ImageEvent;
import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.ImageQueryRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.IImageFlagsUtils;
import image.photos.album.helpers.AlbumHelper;
import image.photos.album.helpers.AlbumPathChecks;
import image.photos.image.helpers.ImageHelper;
import image.photos.image.services.ImageImportOperation;
import image.photos.image.services.ImageImporterService;
import image.photos.infrastructure.database.ImageCUDService;
import image.photos.infrastructure.filestore.FileStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneak;
import static image.infrastructure.messaging.album.AlbumEventTypeEnum.*;
import static image.jpa2x.util.AlbumUtils.albumNameFrom;
import static image.jpa2x.util.PathUtils.fileName;
import static image.photos.image.services.ImageImportProcTypeEnum.HEAVY;

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
		StopWatch sw = new StopWatch();
		sw.start(path.toString());

		String albumName = albumNameFrom(path);

		// determine or create album
		// path este album nou dar nu are poze
		Optional<AlbumEvent> albumEvent = this.findOrCreateAlbum(albumName);

		if (albumEvent.isEmpty()) {
			// new but empty album
			sw.stop();
			return Optional.of(AlbumEvent.of(new Album(albumName), NEW_BUT_EMPTY));
		}

		Album album = albumEvent.get().getEntity();

		// When importing a new album existsAtLeast1ImageChange will
		// always be true because we are skipping (new) empty albums.
		AtomicBoolean isAtLeast1ImageChanged = new AtomicBoolean(false);

		List<String> foundImageNames = Collections.synchronizedList(new ArrayList<>());

		// iterate and process image files
		if (this.albumHelper.isAlbumWithNoFiles(path)) {
			// existing empty album
			log.debug("BEGIN album with no pictures:\n{}", path);
		} else {
			// take only files existing in the album's directory but not sub-directories
			log.debug("BEGIN album has pictures:\n{}", path);

			List<Tuple2<ImageImportOperation<Supplier<ImageEvent>,
					FileNotFoundException>, Path>> heavy = Collections.synchronizedList(new ArrayList<>());
			List<Tuple2<ImageImportOperation<Supplier<ImageEvent>,
					FileNotFoundException>, Path>> lightweight = Collections.synchronizedList(new ArrayList<>());

			// heavy / light lists construction
			this.fileStoreService.walk(path)
					.map(imgFile -> this.imageImporterService
							.importFromFile(imgFile, album).map(it -> Tuples.of(it, imgFile)))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.forEach(it -> (it.getT1().getType().equals(HEAVY) ? heavy : lightweight).add(it));

			int cpus = Runtime.getRuntime().availableProcessors();
			int parallelism = cpus * 3 / 2;
			ExecutorService executorService = Executors.newFixedThreadPool(parallelism);

			// heavy processing (EXIF + db save)
			Mono<Void> heavyMono = Flux.fromIterable(heavy)
					.log()
					.parallel(cpus, cpus)
					.runOn(Schedulers.fromExecutorService(executorService, "light"))
					.log()
					.doOnNext(it -> log.debug("[heavy, before flatMap]"))
					.flatMap(it -> Mono.just(it)
							.doOnNext(it1 -> log.debug("[heavy mono] {}", fileName(it1.getT2())))
							.map(it1 -> sneak(it1.getT1()::getUnsafe).get().getEntity().getName())
							.doOnError(FileNotFoundException.class, t ->
									log.error("File no longer exists:\n{}", t.getMessage()))
							.onErrorResume(h1 -> Mono.empty()))
					.doOnNext(it -> {
						log.debug("[heavy] {}", it);
						foundImageNames.add(it);
					})
					.then();

			// light processing (db save only)
			Mono<Void> lightMono = Flux.fromIterable(lightweight)
					.log()
					.parallel(parallelism - cpus, parallelism - cpus)
					.runOn(Schedulers.fromExecutorService(executorService, "light"))
					.log()
					.doOnNext(it -> log.debug("[light, before flatMap]"))
					.flatMap(it -> Mono.just(it)
							.doOnNext(it1 -> log.debug("[light mono] {}", fileName(it1.getT2())))
							.map(it1 -> sneak(it1.getT1()::getUnsafe).get().getEntity().getName())
							.doOnError(FileNotFoundException.class, t1 ->
									log.error("File no longer exists:\n{}", t1.getMessage()))
							.onErrorResume(it1 -> Mono.empty()))
					.doOnNext(it -> {
						log.debug("[light] {}", it);
						foundImageNames.add(it);
					})
					.then();

			Mono.zipDelayError(it -> it, heavyMono, lightMono).block();
		}

		isAtLeast1ImageChanged.set(!foundImageNames.isEmpty());
		boolean isNewAlbum = albumEvent.get().getType().equals(CREATED);

		// remove db-images having no corresponding file
		if (!isNewAlbum) {
			List<ImageEvent> events = this.removeImagesHavingNoFile(album, foundImageNames);
			isAtLeast1ImageChanged.compareAndSet(false, !events.isEmpty());
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

		sw.stop();
		log.debug("END album:\n{}\n{}", path, sw.shortSummary());

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
