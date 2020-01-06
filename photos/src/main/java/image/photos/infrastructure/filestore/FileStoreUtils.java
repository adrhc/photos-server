package image.photos.infrastructure.filestore;

import com.rainerhahnekamp.sneakythrow.Sneaky;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileStoreUtils {
	static long lastModifiedTime(Path path) {
		return Sneaky.sneak(() -> Files.getLastModifiedTime(path)).toMillis();
	}

	static long fileSize(Path path) {
		return Sneaky.sneak(() -> Files.size(path));
	}
}
