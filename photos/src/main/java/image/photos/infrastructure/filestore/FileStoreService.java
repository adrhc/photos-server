package image.photos.infrastructure.filestore;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStoreService {
	long lastModifiedTime(Path path);

	long fileSize(Path path);

	boolean exists(Path path);

	boolean isDirectory(Path path);

	boolean isEmptyDir(Path path);

	Stream<Path> walk(Path start);

	Stream<Path> walk1thLevel(Path start);
}
