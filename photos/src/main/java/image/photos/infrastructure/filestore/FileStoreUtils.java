package image.photos.infrastructure.filestore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStoreUtils {
	static long lastModifiedTime(Path path) throws IOException {
		return Files.getLastModifiedTime(path).toMillis();
	}

	static long fileSize(Path path) throws IOException {
		return Files.size(path);
	}
}
