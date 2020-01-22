package image.photos.infrastructure.mixed;

import java.io.IOException;
import java.nio.file.Path;

public interface ComplexImageQueries {
	boolean imageExistsInOtherAlbum(Path imgFile, Integer albumId) throws IOException;
}
