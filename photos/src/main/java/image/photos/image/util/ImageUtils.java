package image.photos.image.util;

import image.persistence.entity.Image;

import java.text.MessageFormat;

public class ImageUtils {
	private static MessageFormat relativeFilePathFormatter =
			new MessageFormat("{0}/{1}");
	private static MessageFormat relativeUriPathFormatter =
			new MessageFormat("{0}/{1,number,#}/{2}");

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
