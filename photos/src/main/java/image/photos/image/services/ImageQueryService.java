package image.photos.image.services;

import java.io.IOException;
import java.nio.file.Path;

public interface ImageQueryService {
	boolean imageExistsInOtherAlbum(Path imgFile, Integer albumId) throws IOException;
}
