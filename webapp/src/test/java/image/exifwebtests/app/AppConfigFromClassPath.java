package image.exifwebtests.app;

import image.jpa2x.repositories.AppConfigRepository;
import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.entitytests.IAppConfigSupplier;
import image.photos.infrastructure.database.TransactionalOperation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;

import static exifweb.util.file.ClassPathUtils.pathOf;
import static exifweb.util.file.ClassPathUtils.virtualPathOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;

public abstract class AppConfigFromClassPath implements IAppConfigSupplier {
	protected static final String ALBUMS_ROOT = "classpath:albums-root";
	private static final String JSON_EXPORT_ROOT = "json-root";
	@Autowired
	private TransactionalOperation transact;
	@Autowired
	private AppConfigRepository configRepository;

	@BeforeAll
	public void setup() {
		setupAlbumsRoot();
		setupPhotosJsonPath();
	}

	@Test
	public void albumsRoot() {
		String root = configRepository.findValueByEnumeratedName(AppConfigEnum.albums_path);
		assertThat(root, endsWith(ALBUMS_ROOT.substring("classpath:".length())));
	}

	private void setupPhotosJsonPath() {
		Path jsonRoot = virtualPathOf(JSON_EXPORT_ROOT);
		saveConfig(jsonRoot.toString(), AppConfigEnum.photos_json_FS_path);
	}

	private void setupAlbumsRoot() {
		Path albumsRoot = pathOf(ALBUMS_ROOT);
		saveConfig(albumsRoot.toString(), AppConfigEnum.albums_path);
	}

	protected void saveConfig(String value, AppConfigEnum name) {
		transact.writeWithVoidResult(em -> {
			AppConfig root = configRepository.findByEnumeratedName(name);
			if (root == null) {
				root = entityAppConfigOf(name.getValue(), value);
			} else {
				root.setValue(value);
			}
			configRepository.save(root);
		});
	}
}
