package image.photos.album.helpers;

import image.jpa2x.repositories.appconfig.AppConfigRepository;
import image.photos.infrastructure.filestore.FileStoreService;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneak;

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
		return this.albumsRoot().resolve(albumName);
	}

	public boolean isAlbumWithNoFiles(Path albumPath) {
		return sneak(() -> this.fileStoreService.isEmptyDir(albumPath));
	}
}
