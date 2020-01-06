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
	private final AlbumHelper albumHelper;

	public AlbumPathChecks(FileStoreService fileStoreService, AlbumRepository albumRepository, AlbumHelper albumHelper) {
		this.fileStoreService = fileStoreService;
		this.albumRepository = albumRepository;
		this.albumHelper = albumHelper;
	}

	public boolean isValidAlbumPath(Path path) {
		// cazul in care albumPath este o poza
		if (!this.fileStoreService.isDirectory(path)) {
			log.error("Wrong albumPath (is a file):\n{}", path);
			return false;
		}
		// valid album
		return true;
	}

	public boolean isValidNewAlbumPath(Path path) {
		if (!isValidAlbumPath(path)) {
			return false;
		}
		// check for path to have files
		if (this.albumHelper.isAlbumWithNoFiles(path)) {
			// ne dorim sa fie album nou dar albumPath nu are poze asa ca daca
			// ar fi intr-adevar album nou atunci nu ar avea sens sa-l import
			log.warn("{} este gol!", path);
			return false;
		}
		// check path for not to already be an album
		return this.albumRepository.findByName(albumNameFrom(path)) == null;
	}
}
