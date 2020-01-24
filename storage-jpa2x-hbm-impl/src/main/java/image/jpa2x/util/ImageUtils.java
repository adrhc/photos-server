package image.jpa2x.util;

import image.persistence.entity.Image;

import java.nio.file.Path;
import java.text.MessageFormat;

import static image.jpa2x.util.PathUtils.fileName;

public class ImageUtils {
	private static MessageFormat relativeFilePathFormatter =
			new MessageFormat("{0}/{1}");
	private static MessageFormat relativeUriPathFormatter =
			new MessageFormat("{0}/{1,number,#}/{2}");

	public static String imageNameFrom(Path path) {
		return fileName(path);
	}

	/**
	 * @return albumName/lastModifTime/imgName, see relativeUriPathFormatter
	 */
	public static String relativeUriPathFor(Long lastModifTime, String imgName, String albumName) {
		return relativeUriPathFormatter.format(new Object[]{albumName, lastModifTime, imgName});
	}

	/**
	 * @return albumName/imgName, see relativeFilePathFormatter
	 */
	public static String relativeFilePathFor(Image image) {
		return relativeFilePathFormatter.format(
				new Object[]{image.getAlbum().getName(), image.getName()});
	}
}
