package image.exifweb.image;

import image.exifweb.album.cover.AlbumCover;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by adr on 2/2/18.
 */
@Component
public class ImageUtils {
	private MessageFormat imageURIFormatter = new MessageFormat("{0}/{1}");
	private MessageFormat commURIFormatter = new MessageFormat("{0}/{1,number,#}/{2}");
	@Value("${thumbs.dir}")
	private String thumbsDir;
	@Value("${albums.dir}")
	private String albumsDir;
	@Value("${max.thumb.size}")
	private double maxThumbSize;
	@Value("${max.thumb.size}")
	private int maxThumbSizeInt;

	public void appendImagePaths(List<? extends ImageBasicInfo> imageBasicInfos) {
		for (ImageBasicInfo basicInfo : imageBasicInfos) {
			if (basicInfo.getImgName() == null) {
				continue;
			}
			appendImagePaths(basicInfo);
		}
	}

	public void appendImagePaths(ImageBasicInfo basicInfo) {
		String albumName = basicInfo.getAlbumName();
		Long thumbLastModified = basicInfo.getThumbLastModified().getTime();
		String imgName = basicInfo.getImgName();

		String albumLastModifImg = commURIFormatter.format(
				new Object[]{albumName, thumbLastModified, imgName});

		basicInfo.setThumbPath(imageURIFormatter.format(
				new Object[]{thumbsDir, albumLastModifImg}));
		basicInfo.setImagePath(imageURIFormatter.format(
				new Object[]{albumsDir, albumLastModifImg}));
	}

	public void appendImagePaths(AlbumCover albumCover, Long thumbLastModified) {
		String albumName = albumCover.getAlbumName();
		String imgName = albumCover.getImgName();

		String albumLastModifImg = commURIFormatter.format(
				new Object[]{albumName, thumbLastModified, imgName});

		albumCover.setThumbPath(imageURIFormatter.format(
				new Object[]{thumbsDir, albumLastModifImg}));
	}

	public void appendImageDimensions(List<? extends IImageDimensions> imageDimensions) {
		for (IImageDimensions entity : imageDimensions) {
			appendImageDimensions(entity);
		}
	}

	public void appendImageDimensions(IImageDimensions entity) {
		if (entity.getImageHeight() < entity.getImageWidth()) {
			entity.setImageHeight((int)
					Math.floor(maxThumbSize * entity.getImageHeight() / entity.getImageWidth()));
			entity.setImageWidth(maxThumbSizeInt);
		} else {
			entity.setImageWidth((int)
					Math.floor(maxThumbSize * entity.getImageWidth() / entity.getImageHeight()));
			entity.setImageHeight(maxThumbSizeInt);
		}
	}
}
