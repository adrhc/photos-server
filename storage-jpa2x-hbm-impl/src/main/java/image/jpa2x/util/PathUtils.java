package image.jpa2x.util;

import java.nio.file.Path;

public class PathUtils {
	public static String fileName(Path path) {
		return path.getFileName().toString();
	}

	public static String parentDir(Path path) {
		return path.getParent().getFileName().toString();
	}
}
