package image.exifwebtests.app;

import image.jpa2x.repositories.AppConfigRepository;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.entitytests.IAppConfigSupplier;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;

import static exifweb.util.file.ClassPathUtils.pathOf;

public abstract class AppConfigFromClassPath implements IAppConfigSupplier {
	private static final String ALBUMS_ROOT = "classpath:albums-root";
	@Autowired
	private AppConfigRepository configRepository;

	protected void photosJsonPath(Path tempDir) {
		this.configRepository.updateOrCreate(tempDir.toString(), AppConfigEnum.photos_json_FS_path);
	}

	protected void photosPerPage(int count) {
		this.configRepository.updateOrCreate(String.valueOf(count), AppConfigEnum.photos_per_page);
	}

	protected void defaultAlbumsRoot() {
		this.albumsRoot(pathOf(ALBUMS_ROOT));
	}

	protected void albumsRoot(Path albumsRoot) {
		this.configRepository.updateOrCreate(albumsRoot.toString(), AppConfigEnum.albums_path);
	}
}
