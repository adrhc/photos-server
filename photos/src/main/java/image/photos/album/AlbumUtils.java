package image.photos.album;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneak;

public class AlbumUtils {
	public static boolean emptyAlbum(Path path) {
		return sneak(() -> Files.list(path)).findAny().isEmpty();
	}

	public static String albumName(Path path) {
		return path.getFileName().toString();
	}
}
