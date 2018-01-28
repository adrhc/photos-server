package image.exifweb.album;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.view.AlbumCover;
import image.exifweb.sys.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adr on 1/28/18.
 */
@Service
public class AlbumExporter {
	public static final String ALBUMS_PAGE_JSON = "albums.json";
	public static final String PAGE_COUNT = "pageCount";
	public static final String PHOTOS_PER_PAGE = "photosPerPage";
	private static final Logger logger = LoggerFactory.getLogger(AlbumExporter.class);
	@Inject
	private AppConfigService appConfigService;
	@Inject
	private AlbumService albumService;
	@Inject
	private ObjectMapper jsonMapper;

	public boolean writeJsonForAlbumSafe(String name) {
		Album album = albumService.getAlbumByName(name);
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
		List<AlbumCover> albumCovers = albumService.getAllCovers();
		boolean successForAlbum, existsFail = false, existsSuccess = false;
		for (AlbumCover albumCover : albumCovers) {
			successForAlbum = writeJsonForAlbumSafe(new Album(albumCover));
			existsFail = existsFail || !successForAlbum;
			existsSuccess = existsSuccess || successForAlbum;
		}
		if (existsFail) {
			return existsSuccess ?
					E3ResultTypes.partial : E3ResultTypes.fail;
		} else {
			return E3ResultTypes.success;
		}
	}

	/**
	 * Necesara doar la debug din js/grunt fara serverul java.
	 */
	public boolean writeJsonForAlbumsPageSafe() {
		File file = new File(appConfigService.getConfig("photos json FS path"), ALBUMS_PAGE_JSON);
		file.getParentFile().mkdirs();
		List<AlbumCover> albums = albumService.getAllCovers(true);
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
		logger.debug("BEGIN {}", album.getName());
		if (album.isDeleted()) {
			logger.debug("END (is deleted) {}", album.getName());
			return;
		}
		int pageCount = albumService.getPageCount(null, false, album.getId());
		int photosPerPage = appConfigService.getPhotosPerPage();
		Map<String, Object> map = new HashMap<>();
		map.put(PAGE_COUNT, pageCount);
		map.put(PHOTOS_PER_PAGE, photosPerPage);
		File dir = new File(appConfigService.getConfig("photos json FS path") +
				File.separatorChar + album.getId());
		dir.mkdirs();
		File file = new File(dir, "pageCount.json");
		// write pageCount info
		jsonMapper.writeValue(file, map);
		for (int i = 0; i < pageCount; i++) {
			// write page i + 1 asc
			jsonMapper.writeValue(new File(dir, "asc" + String.valueOf(i + 1) + ".json"),
					albumService.getPage(i + 1, "asc", null, false, album.getId()));
			// write page i + 1 desc
			jsonMapper.writeValue(new File(dir, "desc" + String.valueOf(i + 1) + ".json"),
					albumService.getPage(i + 1, "desc", null, false, album.getId()));
		}
		albumService.clearDirtyForAlbum(album.getId());
		logger.debug("END {}", album.getName());
	}
}
