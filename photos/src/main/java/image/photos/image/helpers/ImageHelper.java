package image.photos.image.helpers;

import image.cdm.album.cover.AlbumCover;
import image.cdm.image.feature.IImageBasicInfo;
import image.cdm.image.feature.IImageDimensions;
import image.persistence.entity.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by adr on 2/2/18.
 */
@Component
@Slf4j
public class ImageHelper {
	private static MessageFormat fullUriPathFormatter =
			new MessageFormat("{0}/{1}");
	private static MessageFormat relativeFilePathFormatter =
			new MessageFormat("{0}/{1}");
	private static MessageFormat relativeUriPathFormatter =
			new MessageFormat("{0}/{1,number,#}/{2}");
	@Value("${thumbs.dir}")
	private String thumbsDir;
	@Value("${albums.dir}")
	private String albumsDir;
	@Value("${max.thumb.size}")
	private double maxThumbSize;
	@Value("${max.thumb.size}")
	private int maxThumbSizeInt;

	private static String relativeUriPathFor(Long lastModifTime, String imgName, String albumName) {
		return relativeUriPathFormatter.format(new Object[]{albumName, lastModifTime, imgName});
	}

	public static String relativeFilePathFor(Image image) {
		return relativeFilePathFormatter.format(
				new Object[]{image.getAlbum().getName(), image.getName()});
	}

	private String fullUriPathForThumb(String relativePath) {
		return fullUriPathFormatter.format(new Object[]{this.thumbsDir, relativePath});
	}

	private String fullUriPathForImage(String relativePath) {
		return fullUriPathFormatter.format(new Object[]{this.albumsDir, relativePath});
	}

	/**
	 * sets thumbPath and imagePath for images
	 */
	public void appendImagePaths(List<? extends IImageBasicInfo> imageBasicInfos) {
		for (IImageBasicInfo basicInfo : imageBasicInfos) {
			if (basicInfo.getImgName() == null) {
				continue;
			}
			appendImagePaths(basicInfo);
		}
	}

	/**
	 * sets thumbPath and imagePath for an image
	 */
	private void appendImagePaths(IImageBasicInfo basicInfo) {
		String albumName = basicInfo.getAlbumName();
		String imgName = basicInfo.getImgName();

		Long thumbLastModified = basicInfo.getThumbLastModified().getTime();
		Long imageLastModified = basicInfo.getDateTime().getTime();

		basicInfo.setThumbPath(thumbPathFor(thumbLastModified, imgName, albumName));
		basicInfo.setImagePath(imagePathFor(imageLastModified, imgName, albumName));
	}

	/**
	 * sets thumbPath for a cover
	 */
	public void appendImagePaths(AlbumCover cover, Long thumbLastModified) {
		String albumName = cover.getAlbumName();
		String imgName = cover.getImgName();

		cover.setThumbPath(thumbPathFor(thumbLastModified, imgName, albumName));
	}

	private String thumbPathFor(Long thumbLastModif, String imgName, String albumName) {
		String relativePath = relativeUriPathFor(thumbLastModif, imgName, albumName);
		return fullUriPathForThumb(relativePath);
	}

	private String imagePathFor(Long imageLastModif, String imgName, String albumName) {
		String relativePath = relativeUriPathFor(imageLastModif, imgName, albumName);
		return fullUriPathForImage(relativePath);
	}

	/**
	 * sets width and height for images
	 */
	public void appendImageDimensions(List<? extends IImageDimensions> imageDimensions) {
		for (IImageDimensions entity : imageDimensions) {
			appendImageDimensions(entity);
		}
	}

	/**
	 * sets width and height for an image
	 */
	public void appendImageDimensions(IImageDimensions entity) {
		if (entity.getImageHeight() < entity.getImageWidth()) {
			entity.setImageHeight((int)
					Math.floor(this.maxThumbSize * entity.getImageHeight() / entity.getImageWidth()));
			entity.setImageWidth(this.maxThumbSizeInt);
		} else {
			entity.setImageWidth((int)
					Math.floor(this.maxThumbSize * entity.getImageWidth() / entity.getImageHeight()));
			entity.setImageHeight(this.maxThumbSizeInt);
		}
	}

	/**
	 * @return fileName having extension as lower or upper case
	 * when lower it makes upper otherwise it makes lower
	 */
	public String changeToOppositeExtensionCase(String fileName) {
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
