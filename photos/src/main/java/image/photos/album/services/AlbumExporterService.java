package image.photos.album.services;

import image.cdm.album.cover.AlbumCover;
import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.AppConfigRepository;
import image.persistence.entity.Album;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.repository.AlbumPageRepository;
import image.persistence.repository.ESortType;
import image.photos.infrastructure.filestore.FileStoreService;
import image.photos.util.status.E3ResultTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by adr on 1/28/18.
 * <p>
 * todo: change the *Safe methods way of work; see writeJsonForAlbumsPage(Consumer<IOException> errorConsumer)
 */
@Service
@Slf4j
public class AlbumExporterService {
	public static final String ALBUMS_PAGE_JSON = "albums.json";
	public static final String PAGE_COUNT = "pageCount";
	public static final String PHOTOS_PER_PAGE = "photosPerPage";
	private final AppConfigRepository appConfigRepository;
	private final AlbumPageRepository albumPageRepository;
	private final AlbumRepository albumRepository;
	private final AlbumPageService albumPageService;
	private final AlbumCoverService albumCoverService;
	private final FileStoreService fileStoreService;

	public AlbumExporterService(AppConfigRepository appConfigRepository, AlbumPageRepository albumPageRepository, AlbumRepository albumRepository, AlbumPageService albumPageService, AlbumCoverService albumCoverService, FileStoreService fileStoreService) {
		this.appConfigRepository = appConfigRepository;
		this.albumPageRepository = albumPageRepository;
		this.albumRepository = albumRepository;
		this.albumPageService = albumPageService;
		this.albumCoverService = albumCoverService;
		this.fileStoreService = fileStoreService;
	}

	public boolean writeJsonForAlbumSafe(String name) {
		Album album = this.albumRepository.findByName(name);
		if (album == null) {
			log.error("Missing album: {}", name);
		}
		return album != null && this.writeJsonForAlbumSafe(album);
	}

	public boolean writeJsonForAlbumSafe(Album album) {
		try {
			this.writeJsonForAlbum(album);
			return true;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			log.debug("failed to write json for: {}", album.getName());
		}
		return false;
	}

	public E3ResultTypes writeJsonForAllAlbumsSafe() {
		List<Album> albums = this.albumRepository.findByDeletedFalseOrderByNameDesc();
		boolean successForAlbum, existsFail = false, existsSuccess = false;
		for (Album album : albums) {
			successForAlbum = this.writeJsonForAlbumSafe(album);
			existsFail = existsFail || !successForAlbum;
			existsSuccess = existsSuccess || successForAlbum;
		}
		if (existsFail) {
			return existsSuccess ?
					E3ResultTypes.PARTIAL : E3ResultTypes.FAIL;
		} else {
			return E3ResultTypes.SUCCESS;
		}
	}

	/**
	 * Necesara doar la debug din js/grunt fara serverul java.
	 */
	public void writeJsonForAlbumsPage(Consumer<IOException> errorConsumer) {
		Path file = Path.of(this.appConfigRepository
				.findValueByEnumeratedName(AppConfigEnum.photos_json_FS_path), ALBUMS_PAGE_JSON);
		try {
			this.fileStoreService.createDirectories(file.getParent());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			log.debug("failed to create directories for: {}", file.getParent());
			errorConsumer.accept(e);
			return;
		}
		List<AlbumCover> albums = this.albumCoverService.getCovers();
		try {
			this.fileStoreService.writeJson(file, albums);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			log.debug("failed to write json for: {}", ALBUMS_PAGE_JSON);
			errorConsumer.accept(e);
		}
	}

	private void writeJsonForAlbum(Album album) throws IOException {
		log.debug("BEGIN id = {}, name = {}", album.getId(), album.getName());

		// prepare pageCount.json data
		int pageCount = this.albumPageRepository.countPages(null, false, false, album.getId());
		Integer photosPerPage = this.appConfigRepository.getPhotosPerPage();
		Map<String, Integer> map = new HashMap<>();
		map.put(PAGE_COUNT, pageCount);
		map.put(PHOTOS_PER_PAGE, photosPerPage);

		// create export dir
		Path dir = Path.of(this.appConfigRepository.findValueByEnumeratedName
				(AppConfigEnum.photos_json_FS_path), album.getId().toString());
		log.debug("export path:\n{}", dir);
		this.fileStoreService.createDirectories(dir);

		// this updates the album's lastUpdate date which will no longer
		// correspond to (asd/desc)N.json pages if put after their writes!
		this.albumRepository.clearDirty(album.getId());

		// write pageCount info
		this.fileStoreService.writeJson(dir.resolve("pageCount.json"), map);

		// write json pages
		for (int i = 0; i < pageCount; i++) {
			log.debug("write page {} asc", (i + 1));
			this.fileStoreService.writeJson(dir.resolve("asc" + (i + 1) + ".json"),
					this.albumPageService.getPage(i + 1, ESortType.ASC, null, false, false, album.getId()));
			log.debug("write page {} desc", (i + 1));
			this.fileStoreService.writeJson(dir.resolve("desc" + (i + 1) + ".json"),
					this.albumPageService.getPage(i + 1, ESortType.DESC, null, false, false, album.getId()));
		}

		log.debug("END {}", album.getName());
	}
}
