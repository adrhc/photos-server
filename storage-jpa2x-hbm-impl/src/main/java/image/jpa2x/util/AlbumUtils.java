package image.jpa2x.util;

import java.nio.file.Path;

import static image.jpa2x.util.PathUtils.fileName;

public class AlbumUtils {
	public static String albumNameFrom(Path path) {
		return fileName(path);
	}
}
