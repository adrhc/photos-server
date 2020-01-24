package image.photos.album.services;

import image.cdm.image.ImageRating;
import image.cdm.image.status.ImageFlagEnum;
import image.infrastructure.messaging.album.AlbumEvent;
import image.infrastructure.messaging.album.AlbumTopic;
import image.infrastructure.messaging.image.ImageEvent;
import image.infrastructure.messaging.image.ImageEventTypeEnum;
import image.jpa2x.repositories.album.AlbumRepository;
import image.jpa2x.repositories.image.ImageRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.IImageFlagsUtils;
import image.photos.album.helpers.AlbumHelper;
import image.photos.album.helpers.AlbumPathChecks;
import image.photos.image.helpers.ImageHelper;
import image.photos.image.services.ImageImporterService;
import image.photos.infrastructure.filestore.FileStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneak;
import static image.infrastructure.messaging.album.AlbumEventTypeEnum.*;
import static image.infrastructure.messaging.util.ImageEventUtils.sortedNamesOf;
import static image.jpa2x.util.AlbumUtils.albumNameFrom;

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
	private final ImageRepository imageRepository;
	private final AlbumRepository albumRepository;
	private final AlbumTopic albumTopic;
	private final AlbumPathChecks albumPathChecks;
	private final AlbumHelper albumHelper;
	private final FileStoreService fileStoreService;

	public AlbumImporterService(ImageHelper imageHelper, ImageImporterService imageImporterService, ImageRepository imageRepository, AlbumRepository albumRepository, AlbumTopic albumTopic, AlbumPathChecks albumPathChecks, AlbumHelper albumHelper, FileStoreService fileStoreService) {
		this.imageHelper = imageHelper;
		this.imageImporterService = imageImporterService;
		this.imageRepository = imageRepository;
		this.albumRepository = albumRepository;
		this.albumTopic = albumTopic;
		this.albumPathChecks = albumPathChecks;
		this.albumHelper = albumHelper;
		this.fileStoreService = fileStoreService;
	}

	/**
	 * import new albums and rescan existing
	 */
	public List<AlbumEvent> importAll() throws IOException {
		return this.importFilteredFromRoot(this.albumPathChecks::isValidAlbumPath);
	}

	/**
	 * import new albums only
	 */
	public List<AlbumEvent> importNewAlbums() throws IOException {
		return this.importFilteredFromRoot(this.albumPathChecks::isValidNewAlbumPath);
	}

	/**
	 * import new album or rescan existing
	 */
	public AlbumEvent importByAlbumName(String albumName) {
		Path path = this.albumHelper.absolutePathOf(albumName);
		if (!this.albumPathChecks.isValidAlbumPath(path)) {
			return AlbumEvent.of(new Album(albumName), MISSING_PATH);
		}
		return this.safelyImportByAlbumPath(path);
	}

	/**
	 * Filters album paths to be imported.
	 */
	private List<AlbumEvent> importFilteredFromRoot(Predicate<Path> albumsFilter) throws IOException {
		List<AlbumEvent> albumEvents = new ArrayList<>();
		Path root = this.albumHelper.albumsRoot();
		this.fileStoreService.walk1thLevel(root)
				.filter(albumsFilter)
				.sorted(Collections.reverseOrder())
				.forEach(path -> albumEvents.add(this.safelyImportByAlbumPath(path)));
		return albumEvents;
	}

	private AlbumEvent safelyImportByAlbumPath(Path path) {
		try {
			return this.importByAlbumPath(path);
		} catch (IOException e) {
			return AlbumEvent.of(new Album(albumNameFrom(path)), MISSING_PATH);
		}
	}

	/**
	 * By now we already checked that path is a valid album path.
	 *
	 * @return true means "path/album created"
	 */
	private AlbumEvent importByAlbumPath(Path path) throws IOException {
		String albumName = albumNameFrom(path);

		// find or create the albumName
		Optional<AlbumEvent> albumEventOpt = this.findOrCreateAlbum(albumName);

		// see in conjunction with findOrCreateAlbum()
		if (albumEventOpt.isEmpty()) {
			log.debug("New but empty (no files) album:\n{}", path);
			return AlbumEvent.of(new Album(albumName), NEW_BUT_EMPTY);
		}

		AlbumEvent albumEvent = albumEventOpt.get();

		// see in conjunction with findOrCreateAlbum()
		if (albumEvent.isTypeOf(UPDATED) && this.albumHelper.isAlbumWithNoFiles(path)) {
			log.debug("Already existing empty (no files) album\n{}", path);
			return albumEvent;
		}

		StopWatch stopWatch = new StopWatch(albumName);
		stopWatch.start(albumName);

		// iterate and process image files
		log.debug("BEGIN album has pictures:\n{}", path);

		boolean isNewAlbum = albumEvent.isTypeOf(CREATED);
		Album album = albumEvent.getEntity();

		int cpus = Runtime.getRuntime().availableProcessors();

		// take files existing in the album's directory and sub-directories
		Flux.fromStream(this.fileStoreService.walk(path))

				// Prepare this Flux by dividing data on a number of 'rails' matching the number of CPU cores, in a round-robin fashion.
				.parallel(cpus, cpus)
				// Specifies where each 'rail' will observe its incoming values with possibly work-stealing and a given prefetch amount.
				.runOn(Schedulers.newBoundedElastic(cpus, Integer.MAX_VALUE, "import"), 1)

				// importing image from file
				.log()
				.doOnNext(it -> log.debug("[before import] {}", it))
				.flatMap(it -> Mono
						.just(it)
						.map(it1 -> sneak(() -> this.imageImporterService.importFromFile(it, album)))
						.onErrorContinue(e -> e instanceof FileNotFoundException
										|| e instanceof NoSuchFileException,
								(t, o) -> log.error("File is missing:\n{}", it))
				)
				.filter(Optional::isPresent)
				.map(Optional::get)

				.sequential().collectList()

				.doOnNext(events -> {
					// any image was imported or changed in any way?
					boolean isAtLeast1ImageChanged = events.stream()
							.anyMatch(ie -> !ie.isTypeOf(ImageEventTypeEnum.NOTHING));

					// remove db-images having no corresponding file
					if (!isNewAlbum) {
						// on any change this dirties the related album
						// ImageRepository.[changeName|safelyDeleteImage|markDeleted] dirty the album
						List<ImageEvent> eventsOnRemoval = this.removeImagesHavingNoFile(
								album, sortedNamesOf(events));
						// mark any changes
						isAtLeast1ImageChanged = isAtLeast1ImageChanged || !eventsOnRemoval.isEmpty();
					}

					// album event emission (see AlbumExporterSubscription)
					if (isAtLeast1ImageChanged) {
						this.albumTopic.emit(albumEvent);
					}
				})

				.block();

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
		List<Image> images = this.imageRepository.findByAlbumId(album.getId());
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
				events.add(this.imageRepository.changeName(oppositeExtensionCase, image.getId()));
			} else if (this.areEquals(image.getFlags(), ImageFlagEnum.DEFAULT) ||
					image.getRating() != ImageRating.MIN_RATING) {
				// not found (flags & rating not changed): purge image from DB
				log.debug("poza din DB ({}) nu exista in file system: sterg din DB", dbName);
				events.add(this.imageRepository.safelyDeleteImage(image.getId()));
			} else {
				// not found (flags or rating changed): logically delete image
				log.debug("poza din DB ({}) nu exista in file system: marchez ca stearsa", dbName);
				events.add(this.imageRepository.markDeleted(image.getId()));
			}
		});
		log.debug("END {}", album.getName());
		return events;
	}
}
