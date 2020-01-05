package image.photostests.overrides.infrastructure.filestore;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.photos.infrastructure.filestore.FileStoreServiceImpl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileStoreServiceTestImpl extends FileStoreServiceImpl implements FileStoreServiceTest {
	private List<Path> size1Path = Collections.synchronizedList(new ArrayList<>());
	private List<Path> lastModifiedTime = Collections.synchronizedList(new ArrayList<>());

	public FileStoreServiceTestImpl(ObjectMapper mapper) {
		super(mapper);
	}

	@Override
	public long lastModifiedTime(Path path) {
		if (this.lastModifiedTime.stream()
				.anyMatch(p -> p.toString().equals(path.toString()))) {
			return specialLastModifiedTime;
		}
		return 1577922958000L;
	}

	@Override
	public long fileSize(Path path) {
		if (this.size1Path.stream()
				.anyMatch(p -> p.toString().equals(path.toString()))) {
			return 1;
		}
		return 0;
	}

	@Override
	public boolean exists(Path path) {
		return true;
	}

	@Override
	public void setSpecialLastModifiedTimeForPath(Path special) {
		this.lastModifiedTime.add(special);
	}

	@Override
	public void addSize1Path(Path size1Path) {
		this.size1Path.add(size1Path);
	}
}
