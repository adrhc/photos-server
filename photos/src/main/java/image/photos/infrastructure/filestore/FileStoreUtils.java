package image.photos.infrastructure.filestore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileStoreUtils {
	static long lastModifiedTime(Path path) throws IOException {
		return Files.getLastModifiedTime(path).toMillis();
	}

	static long fileSize(Path path) throws IOException {
		return Files.size(path);
	}

	/**
	 * @return fileName having extension as lower or upper case
	 * when lower it makes upper otherwise it makes lower
	 */
	public static String changeToOppositeExtensionCase(String fileName) {
		StringBuilder sb = new StringBuilder();
		int idx = fileName.lastIndexOf(".");
		if (idx < 0) {
			return fileName;
		}
		sb.append(fileName, 0, idx);
		String pointAndExtension = fileName.substring(idx);
		if (pointAndExtension.equals(pointAndExtension.toLowerCase())) {
			sb.append(pointAndExtension.toUpperCase());
		} else {
			sb.append(pointAndExtension.toLowerCase());
		}
		return sb.toString();
	}
}
