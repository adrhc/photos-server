package image.photos.infrastructure.filestore;

import java.nio.file.FileVisitOption;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStoreService {
	long lastModifiedTime(Path path);

	long fileSize(Path path);

	String fileName(Path path);

	boolean exists(Path path);

	boolean isDirectory(Path path);

	boolean isEmptyDir(Path path);

	Stream<Path> walk(Path start, FileVisitOption... options);
}
