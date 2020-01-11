package image.photos.infrastructure.filestore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

@Service
@Slf4j
public class FileStoreServiceImpl implements FileStoreService {
	private final ObjectMapper jsonMapper;

	public FileStoreServiceImpl(ObjectMapper jsonMapper) {this.jsonMapper = jsonMapper;}

	@Override
	public long lastModifiedTime(Path path) throws IOException {
		return FileStoreUtils.lastModifiedTime(path);
	}

	@Override
	public long fileSize(Path path) throws IOException {
		return FileStoreUtils.fileSize(path);
	}

	@Override
	public boolean exists(Path path) {
		return Files.exists(path);
	}

	@Override
	public boolean isDirectory(Path path) {
		return Files.isDirectory(path);
	}

	@Override
	public boolean isEmptyDir(Path path) throws IOException {
		return Files.list(path).findAny().isEmpty();
	}

	@Override
	public Stream<Path> walk(Path start) throws IOException {
		return Files.walk(start, FOLLOW_LINKS).filter(p -> !p.equals(start));
	}

	@Override
	public Stream<Path> walk1thLevel(Path start) throws IOException {
		return Files.walk(start, 1, FOLLOW_LINKS).filter(p -> !p.equals(start));
	}

	@Override
	public Path createDirectories(Path path) throws IOException {
		return Files.createDirectories(path);
	}

	public <T> void writeJson(Path path, T value) throws IOException {
		try (OutputStream fos = Files.newOutputStream(path)) {
			this.jsonMapper.writeValue(fos, value);
		}
	}

	public <T> T readJson(Path path) throws IOException {
		try (InputStream fis = Files.newInputStream(path)) {
			return this.jsonMapper.readValue(fis, new TypeReference<>() {});
		}
	}

	public <T> List<T> readJsonAsList(Path path, TypeReference<List<T>> typeReference) throws IOException {
		try (InputStream fis = Files.newInputStream(path)) {
			return this.jsonMapper.readValue(fis, typeReference);
		}
	}
}
