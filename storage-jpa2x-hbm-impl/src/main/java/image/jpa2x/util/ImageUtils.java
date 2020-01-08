package image.jpa2x.util;

import java.nio.file.Path;

import static image.jpa2x.util.PathUtils.fileName;

public class ImageUtils {
	public static String imageNameFrom(Path path) {
		return fileName(path);
	}
}
