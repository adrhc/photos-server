package image.photos.album.importing;

import image.cdm.image.status.EImageStatus;
import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.ImageRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.IImageFlagsUtils;
import image.photos.album.AlbumHelper;
import image.photos.events.album.AlbumEvent;
import image.photos.events.album.AlbumEventTypeEnum;
import image.photos.events.album.AlbumTopic;
import image.photos.events.image.ImageEvent;
import image.photos.events.image.ImageEventTypeEnum;
import image.photos.events.image.ImageTopic;
import image.photos.events.image.importing.ImageImporterService;
import image.photos.image.ImageHelper;
import image.photos.util.ValueHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import reactor.core.Disposable;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneaked;
import static image.photos.album.AlbumUtils.albumName;
import static image.photos.album.AlbumUtils.emptyAlbum;
import static image.photos.events.image.ImageEventTypeEnum.DELETED;
import static image.photos.events.image.ImageEventTypeEnum.MARKED_DELETED;

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
	private final ImageTopic imageTopic;
	private final Predicates predicates;
	private final AlbumHelper albumHelper;

	public AlbumImporterService(ImageHelper imageHelper, ImageImporterService imageImporterService, ImageRepository imageRepository, AlbumRepository albumRepository, AlbumTopic albumTopic, ImageTopic imageTopic, Predicates predicates, AlbumHelper albumHelper) {
		this.imageHelper = imageHelper;
		this.imageImporterService = imageImporterService;
		this.imageRepository = imageRepository;
		this.albumRepository = albumRepository;
		this.albumTopic = albumTopic;
		this.imageTopic = imageTopic;
		this.predicates = predicates;
		this.albumHelper = albumHelper;
	}

	/**
	 * import new albums and rescan existing
	 */
	public void importAll() {
		importFilteredFromRoot(this.predicates.VALID_ALBUM_PATH);
	}

	/**
	 * import new albums only
	 */
	public void importNewAlbums() {
		importFilteredFromRoot(this.predicates.VALID_NEW_ALBUM_PATH);
	}

	/**
	 * import new album or rescan existing
	 */
	public void importByAlbumName(String albumName) {
		Path path = this.albumHelper.fullPath(albumName);
		if (!this.predicates.VALID_ALBUM_PATH.test(path)) {
			throw new UnsupportedOperationException("Wrong album path:\n" + path);
		}
		importByAlbumPath(path);
	}

	/**
	 * Filters album paths to be imported.
	 *
	 * @param albumsFilter
	 */
	private void importFilteredFromRoot(Predicate<Path> albumsFilter) {
		Path root = this.albumHelper.rootPath();
		sneaked(() ->
				Files.walk(root, FileVisitOption.FOLLOW_LINKS)
						.filter(albumsFilter)
						.sorted(Collections.reverseOrder())
						.forEach(this::importByAlbumPath))
				.run();
	}

	private Optional<AlbumEvent> findOrCreate(String albumName) {
		Album album = this.albumRepository.findByName(albumName);
		if (album != null) {
			// already existing album
			return Optional.of(AlbumEvent.builder().album(album).build());
		}
		if (emptyAlbum(this.albumHelper.fullPath(albumName))) {
			// new empty album
			return Optional.empty();
		}
		// creem un nou album (path aferent contine poze)
		album = this.albumRepository.createByName(albumName);
		return Optional.of(AlbumEvent.builder()
				.type(AlbumEventTypeEnum.CREATED).album(album).build());
	}

	/**
	 * By now we already checked that path is a valid album path.
	 */
	private void importByAlbumPath(Path path) {
		StopWatch sw = new StopWatch();
		sw.start(path.toString());

		// determine or create album
		// path este album nou dar nu are poze
		Optional<AlbumEvent> albumEvent = findOrCreate(albumName(path));
		if (albumEvent.isEmpty()) {
			// album nou dar gol
			sw.stop();
			return;
		}

		Album album = albumEvent.get().getAlbum();
		boolean isNewAlbum = albumEvent.get().getType().equals(AlbumEventTypeEnum.CREATED);

		// Preparing an imageEvents-listener used to
		// determine whether exists any image changes.
		// When importing a new album existsAtLeast1ImageChange will
		// always be true because we are not importing empty albums.
		ValueHolder<Boolean> isAtLeast1ImageChanged = ValueHolder.of(false);
		Disposable subscription = this.imageTopic
				.imageEventsByType(EnumSet.allOf(ImageEventTypeEnum.class))
				.take(1L)
				.subscribe(event -> isAtLeast1ImageChanged.setValue(true));

		// iterate and process image files
		List<String> foundImages = new ArrayList<>();
		if (emptyAlbum(path)) {
			// existing empty album
			log.debug("BEGIN album with no pictures:\n{}", path);
		} else {
			// take only files existing in the album's directory but not sub-directories
			log.debug("BEGIN album has pictures:\n{}", path);
			sneaked(() ->
					Files.walk(path, FileVisitOption.FOLLOW_LINKS)
							.forEach(file -> {
								if (this.imageImporterService.importImageFromFile(file, album)) {
									foundImages.add(file.getFileName().toString());
								}
							}))
					.run();
		}

		// remove db-images having no corresponding file
		if (!isNewAlbum) {
			deleteNotFoundImages(foundImages, album);
		}

		// todo: make sure to dispose even when an exception occurs
		subscription.dispose();

		// see AlbumExporterService.postConstruct
		if (isNewAlbum) {
			this.albumTopic.emit(albumEvent.get());
		} else if (isAtLeast1ImageChanged.getValue()) {
			this.albumTopic.emit(AlbumEvent.builder()
					.type(AlbumEventTypeEnum.UPDATED)
					.album(album).build());
		}

		sw.stop();
		log.debug("END album:\n{}\n{}", path, sw.shortSummary());
	}

	/**
	 * Cached Album is detached so can't be used as persistent as required in this method.
	 *
	 * @param foundImageNames
	 */
	private void deleteNotFoundImages(List<String> foundImageNames, Album album) {
		log.debug("BEGIN {}", album.getName());
		List<Image> images = this.imageRepository.findByAlbumId(album.getId());
//		List<Image> images = this.imageService.getImages(album.getId());
		images.forEach(image -> {
			String dbName = image.getName();
			int fsNameIdx = foundImageNames.indexOf(dbName);
			if (fsNameIdx >= 0) {
				// imagine existenta in DB cu acelas nume ca in file system
				return;
			}
			String oppositeExtensionCase = this.imageHelper.changeToOppositeExtensionCase(dbName);
			fsNameIdx = foundImageNames.indexOf(oppositeExtensionCase);
			ImageEvent.ImageEventBuilder imgEvBuilder =
					ImageEvent.builder().album(album).image(image);
			if (fsNameIdx >= 0) {
				log.debug("poza din DB ({}) cu nume diferit in file system ({}):\nactualizez in DB cu {}",
						dbName, oppositeExtensionCase, oppositeExtensionCase);
				this.imageRepository.changeName(oppositeExtensionCase, image.getId());
				this.imageTopic.emit(imgEvBuilder.type(ImageEventTypeEnum.UPDATED).build());
				return;
			}
			if (areEquals(image.getFlags(), EImageStatus.DEFAULT)) {
				// status = 0
				log.debug("poza din DB ({}) nu exista in file system: sterg din DB", dbName);
				this.imageRepository.safelyDeleteImage(image.getId());
				this.imageTopic.emit(imgEvBuilder.type(DELETED).build());
				return;
			}
			// status != 0 (adica e o imagine "prelucrata")
			log.debug("poza din DB ({}) nu exista in file system: marchez ca stearsa", dbName);
			if (this.imageRepository.markDeleted(image.getId())) {
				this.imageTopic.emit(imgEvBuilder.type(MARKED_DELETED).build());
			}
		});
		log.debug("END {}", album.getName());
	}
}
