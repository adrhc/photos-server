package image.photos.album.helpers;

import image.jpa2x.repositories.AlbumRepository;
import image.photos.infrastructure.filestore.FileStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

import static image.jpa2x.util.AlbumUtils.albumNameFrom;

@Component
@Slf4j
public class AlbumPathChecks {
	private final FileStoreService fileStoreService;
	private final AlbumRepository albumRepository;

	public AlbumPathChecks(FileStoreService fileStoreService, AlbumRepository albumRepository) {
		this.fileStoreService = fileStoreService;
		this.albumRepository = albumRepository;
	}

	public boolean isValidAlbumPath(Path path) {
		// missing path
		if (!this.fileStoreService.exists(path)) {
			log.error("Missing albumPath:\n{}", path);
			return false;
		}
		// cazul in care albumPath este o poza
		if (!this.fileStoreService.isDirectory(path)) {
			log.error("Album path is not a directory:\n{}", path);
			return false;
		}
		// valid album
		return true;
	}

	public boolean isValidNewAlbumPath(Path path) {
		if (!this.isValidAlbumPath(path)) {
			return false;
		}
		// check path for not to already be an album
		return this.albumRepository.findByName(albumNameFrom(path)) == null;
	}
}
