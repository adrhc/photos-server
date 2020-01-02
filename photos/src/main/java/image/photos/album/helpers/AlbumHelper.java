package image.photos.album.helpers;

import image.jpa2x.repositories.AppConfigRepository;
import image.photos.infrastructure.filestore.FileStoreService;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class AlbumHelper {
	private final AppConfigRepository appConfigRepository;
	private final FileStoreService fileStoreService;

	public AlbumHelper(AppConfigRepository appConfigRepository, FileStoreService fileStoreService) {
		this.appConfigRepository = appConfigRepository;
		this.fileStoreService = fileStoreService;
	}

	public Path albumsRoot() {
		return Path.of(this.appConfigRepository.getAlbumsPath());
	}

	public Path absolutePathOf(String albumName) {
		return albumsRoot().resolve(albumName);
	}

	public boolean isAlbumWithNoFiles(Path albumPath) {
		return this.fileStoreService.isEmptyDir(albumPath);
	}

	public static String albumNameFrom(Path path) {
		return path.getFileName().toString();
	}
}
