package image.photos.album;

import java.nio.file.Path;

public class AlbumUtils {
	public static String albumName(Path path) {
		return path.getFileName().toString();
	}
}
