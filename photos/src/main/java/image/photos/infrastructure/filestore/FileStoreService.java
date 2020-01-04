package image.photos.infrastructure.filestore;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStoreService {
	long lastModifiedTime(Path path);

	long fileSize(Path path);

	boolean exists(Path path);

	boolean isDirectory(Path path);

	boolean isEmptyDir(Path path) throws IOException;

	Stream<Path> walk(Path start) throws IOException;

	Stream<Path> walk1thLevel(Path start) throws IOException;

	Path createDirectories(Path path) throws IOException;
}
