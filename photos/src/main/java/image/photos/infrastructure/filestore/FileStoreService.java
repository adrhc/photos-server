package image.photos.infrastructure.filestore;

import com.fasterxml.jackson.core.type.TypeReference;
import image.cdm.album.page.AlbumPage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
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

	<T> void writeJson(Path path, T value) throws IOException;

	<T> T readJson(Path path) throws IOException;

	<T> List<T> readJsonAsList(Path path, TypeReference<List<T>> typeReference) throws IOException;
}
