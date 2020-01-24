package image.jpa2x.helper;

import image.cdm.album.cover.AlbumCover;
import image.cdm.image.feature.IImageBasicInfo;
import image.cdm.image.feature.IImageDimensions;
import image.jpa2x.util.ImageUtils;
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
	@Value("${thumbs.dir}")
	private String thumbsDir;
	@Value("${albums.dir}")
	private String albumsDir;
	@Value("${max.thumb.size}")
	private double maxThumbSize;
	@Value("${max.thumb.size}")
	private int maxThumbSizeInt;

	/**
	 * sets thumbPath and imagePath for images
	 */
	public void appendImagePaths(List<? extends IImageBasicInfo> imageBasicInfos) {
		for (IImageBasicInfo basicInfo : imageBasicInfos) {
			if (basicInfo.getImgName() == null) {
				continue;
			}
			this.appendImagePaths(basicInfo);
		}
	}

	/**
	 * sets thumbPath for a cover
	 */
	public void appendImagePaths(AlbumCover cover, Long thumbLastModified) {
		String albumName = cover.getAlbumName();
		String imgName = cover.getImgName();

		cover.setThumbPath(this.thumbUriPathFor(thumbLastModified, imgName, albumName));
	}

	/**
	 * sets width and height for images
	 */
	public void appendImageDimensions(List<? extends IImageDimensions> imageDimensions) {
		for (IImageDimensions entity : imageDimensions) {
			this.appendImageDimensions(entity);
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
	 * @param relativePath is albumName/lastModifTime/imgName
	 *                     see also relativeUriPathFor(), relativeUriPathFormatter
	 */
	private String fullUriPathForThumb(String relativePath) {
		return fullUriPathFormatter.format(new Object[]{this.thumbsDir, relativePath});
	}

	/**
	 * @param relativePath is albumName/lastModifTime/imgName
	 *                     see also relativeUriPathFor(), relativeUriPathFormatter
	 */
	private String fullUriPathForImage(String relativePath) {
		return fullUriPathFormatter.format(new Object[]{this.albumsDir, relativePath});
	}

	/**
	 * sets thumbPath and imagePath for an image
	 */
	private void appendImagePaths(IImageBasicInfo basicInfo) {
		String albumName = basicInfo.getAlbumName();
		String imgName = basicInfo.getImgName();

		Long thumbLastModified = basicInfo.getThumbLastModified().getTime();
		Long imageLastModified = basicInfo.getDateTime().getTime();

		basicInfo.setThumbPath(this.thumbUriPathFor(thumbLastModified, imgName, albumName));
		basicInfo.setImagePath(this.imageUriPathFor(imageLastModified, imgName, albumName));
	}

	private String thumbUriPathFor(Long thumbLastModif, String imgName, String albumName) {
		String relativePath = ImageUtils.relativeUriPathFor(thumbLastModif, imgName, albumName);
		return this.fullUriPathForThumb(relativePath);
	}

	private String imageUriPathFor(Long imageLastModif, String imgName, String albumName) {
		String relativePath = ImageUtils.relativeUriPathFor(imageLastModif, imgName, albumName);
		return this.fullUriPathForImage(relativePath);
	}
}
