package image.photos.infrastructure.filestore;

import org.springframework.stereotype.Service;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneak;

@Service
public class FileStoreServiceImpl implements FileStoreService {
	@Override
	public long lastModifiedTime(Path path) {
		return PathUtils.lastModifiedTime(path);
	}

	@Override
	public long fileSize(Path path) {
		return PathUtils.fileSize(path);
	}

	@Override
	public String fileName(Path path) {
		return PathUtils.fileName(path);
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
	public boolean isEmptyDir(Path path) {
		return sneak(() -> Files.list(path)).findAny().isEmpty();
	}

	@Override
	public Stream<Path> walk(Path start, FileVisitOption... options) {
		return sneak(() -> Files.walk(start, options));
	}
}
