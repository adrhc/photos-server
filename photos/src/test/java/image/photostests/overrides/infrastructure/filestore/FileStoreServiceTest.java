package image.photostests.overrides.infrastructure.filestore;

import java.nio.file.Path;

public interface FileStoreServiceTest {
	long specialLastModifiedTime = 1555000000000L;

	void addSize1Path(Path size1Path);

	void setSpecialLastModifiedTimeForPath(Path special);
}
