package image.photos.infrastructure.filestore;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneak;

public class PathUtils {
	static long lastModifiedTime(Path path) {
		return sneak(() -> Files.getLastModifiedTime(path)).toMillis();
	}

	static long fileSize(Path path) {
		return sneak(() -> Files.size(path));
	}

	public static String fileName(Path path) {
		return path.getFileName().toString();
	}

	public static String parentDir(Path path) {
		return path.getParent().getFileName().toString();
	}
}
