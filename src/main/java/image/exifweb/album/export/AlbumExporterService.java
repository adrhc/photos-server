package image.exifweb.album.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.exifweb.album.cover.AlbumCover;
import image.exifweb.album.cover.AlbumCoverService;
import image.exifweb.album.events.AlbumEventsEmitter;
import image.exifweb.album.page.AlbumPageService;
import image.exifweb.appconfig.AppConfigService;
import image.exifweb.system.persistence.entities.Album;
import image.exifweb.system.persistence.repositories.AlbumPageRepository;
import image.exifweb.system.persistence.repositories.AlbumRepository;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static image.exifweb.album.events.EAlbumEventType.ALBUM_IMPORTED;

/**
 * Created by adr on 1/28/18.
 */
@Service
public class AlbumExporterService {
	public static final String ALBUMS_PAGE_JSON = "albums.json";
	public static final String PAGE_COUNT = "pageCount";
	public static final String PHOTOS_PER_PAGE = "photosPerPage";
	private static final Logger logger = LoggerFactory.getLogger(AlbumExporterService.class);
	@Inject
	private AppConfigService appConfigService;
	@Inject
	private AlbumPageRepository albumPageRepository;
	@Inject
	private AlbumRepository albumRepository;
	@Inject
	private AlbumPageService albumPageService;
	@Inject
	private AlbumEventsEmitter albumEventsEmitter;
	@Inject
	private ObjectMapper jsonMapper;
	@Inject
	private AlbumCoverService albumCoverService;

	public boolean writeJsonForAlbumSafe(String name) {
		Album album = albumRepository.getAlbumByName(name);
		if (album == null) {
			logger.error("Missing album: {}", name);
		}
		return album != null && writeJsonForAlbumSafe(album);
	}

	public boolean writeJsonForAlbumSafe(Album album) {
		try {
			writeJsonForAlbum(album);
			return true;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			logger.debug("failed to write json for: {}", album.getName());
		}
		return false;
	}

	public E3ResultTypes writeJsonForAllAlbumsSafe() {
		List<Album> albums = albumRepository.getAlbumsOrderedByName();
		boolean successForAlbum, existsFail = false, existsSuccess = false;
		for (Album album : albums) {
			successForAlbum = writeJsonForAlbumSafe(album);
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
	public boolean writeJsonForAlbumsPageSafe() {
		File file = new File(appConfigService.getConfig("photos json FS path"), ALBUMS_PAGE_JSON);
		file.getParentFile().mkdirs();
		List<AlbumCover> albums = albumCoverService.getCovers();
		try {
			jsonMapper.writeValue(file, albums);
			return true;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			logger.debug("failed to write json for: {}", ALBUMS_PAGE_JSON);
		}
		return false;
	}

	private void writeJsonForAlbum(Album album) throws IOException {
		logger.debug("BEGIN id = {}, name = {}", album.getId(), album.getName());
		int pageCount = albumPageRepository.getPageCount(null, false, false, album.getId());
		int photosPerPage = appConfigService.getPhotosPerPage();
		Map<String, Object> map = new HashMap<>();
		map.put(PAGE_COUNT, pageCount);
		map.put(PHOTOS_PER_PAGE, photosPerPage);
		File dir = new File(appConfigService.getConfig("photos json FS path"),
				album.getId().toString());
		dir.mkdirs();
		File file = new File(dir, "pageCount.json");
		// write pageCount info
		jsonMapper.writeValue(file, map);
		for (int i = 0; i < pageCount; i++) {
			logger.debug("write page {} asc", (i + 1));
			jsonMapper.writeValue(new File(dir, "asc" + String.valueOf(i + 1) + ".json"),
					albumPageService.getPage(i + 1, "asc", null, false, false, album.getId()));
			logger.debug("write page {} desc", (i + 1));
			jsonMapper.writeValue(new File(dir, "desc" + String.valueOf(i + 1) + ".json"),
					albumPageService.getPage(i + 1, "desc", null, false, false, album.getId()));
		}
		albumRepository.clearDirtyForAlbum(album.getId());
		logger.debug("END {}", album.getName());
	}

	@PostConstruct
	public void postConstruct() {
		albumEventsEmitter.albumEventsByTypes(false, ALBUM_IMPORTED)
				.observeOn(Schedulers.io())
				.subscribe((ae) -> writeJsonForAlbumSafe(ae.getAlbum()),
						t -> {
							logger.error(t.getMessage(), t);
							logger.error("[{}]", ALBUM_IMPORTED.name());
						});
	}
}