package image.photos.album.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.cdm.album.cover.AlbumCover;
import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.AppConfigRepository;
import image.persistence.entity.Album;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.repository.AlbumPageRepository;
import image.persistence.repository.ESortType;
import image.photos.infrastructure.events.album.AlbumTopic;
import image.photos.util.status.E3ResultTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static image.photos.infrastructure.events.album.AlbumEventTypeEnum.CREATED;
import static image.photos.infrastructure.events.album.AlbumEventTypeEnum.UPDATED;

/**
 * Created by adr on 1/28/18.
 */
@Service
public class AlbumExporterService {
	public static final String ALBUMS_PAGE_JSON = "albums.json";
	public static final String PAGE_COUNT = "pageCount";
	public static final String PHOTOS_PER_PAGE = "photosPerPage";
	private static final Logger logger = LoggerFactory.getLogger(AlbumExporterService.class);
	private Disposable albumTopicSubscr;
	@Autowired
	private AppConfigRepository appConfigRepository;
	@Autowired
	private AlbumPageRepository albumPageRepository;
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private AlbumPageService albumPageService;
	@Autowired
	private AlbumTopic albumTopic;
	@Autowired
	private ObjectMapper jsonMapper;
	@Autowired
	private ExecutorService executorService;
	@Autowired
	private AlbumCoverService albumCoverService;

	public boolean writeJsonForAlbumSafe(String name) {
		Album album = this.albumRepository.findByName(name);
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
		List<Album> albums = this.albumRepository.findByDeletedFalseOrderByNameDesc();
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
		File file = new File(this.appConfigRepository.findValueByEnumeratedName(AppConfigEnum.photos_json_FS_path), ALBUMS_PAGE_JSON);
		file.getParentFile().mkdirs();
		List<AlbumCover> albums = this.albumCoverService.getCovers();
		try {
			this.jsonMapper.writeValue(file, albums);
			return true;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			logger.debug("failed to write json for: {}", ALBUMS_PAGE_JSON);
		}
		return false;
	}

	private void writeJsonForAlbum(Album album) throws IOException {
		logger.debug("BEGIN id = {}, name = {}", album.getId(), album.getName());
		int pageCount = this.albumPageRepository.countPages(null, false, false, album.getId());
		Integer photosPerPage = this.appConfigRepository.getPhotosPerPage();
		Map<String, Object> map = new HashMap<>();
		map.put(PAGE_COUNT, pageCount);
		map.put(PHOTOS_PER_PAGE, photosPerPage);
		File dir = new File(this.appConfigRepository.findValueByEnumeratedName(AppConfigEnum.photos_json_FS_path),
				album.getId().toString());
		dir.mkdirs();
		File file = new File(dir, "pageCount.json");
		// write pageCount info
		this.jsonMapper.writeValue(file, map);
		for (int i = 0; i < pageCount; i++) {
			logger.debug("write page {} asc", (i + 1));
			this.jsonMapper.writeValue(new File(dir, "asc" + String.valueOf(i + 1) + ".json"),
					this.albumPageService.getPage(i + 1, ESortType.ASC, null, false, false, album.getId()));
			logger.debug("write page {} desc", (i + 1));
			this.jsonMapper.writeValue(new File(dir, "desc" + String.valueOf(i + 1) + ".json"),
					this.albumPageService.getPage(i + 1, ESortType.DESC, null, false, false, album.getId()));
		}
		this.albumRepository.clearDirtyForAlbum(album.getId());
		logger.debug("END {}", album.getName());
	}

	@PostConstruct
	public void postConstruct() {
		this.albumTopicSubscr = this.albumTopic
				.albumEventsByTypes(false,
						EnumSet.of(CREATED, UPDATED))
				.publishOn(Schedulers.fromExecutor(this.executorService))
				.subscribe((ae) -> writeJsonForAlbumSafe(ae.getAlbum()),
						t -> logger.error(t.getMessage(), t));
	}

	@PreDestroy
	public void preDestroy() {
		this.albumTopicSubscr.dispose();
	}
}
