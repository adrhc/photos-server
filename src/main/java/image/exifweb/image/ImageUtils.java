package image.exifweb.image;

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

	public void appendImagePaths(List<? extends ImageBasicInfo> thumbs) {
		String albumName, imgName, albumLastModifImg;
		Long thumbLastModified;
		for (ImageBasicInfo basicInfo : thumbs) {
			if (basicInfo.getImgName() == null) {
				continue;
			}

			albumName = basicInfo.getAlbumName();
			thumbLastModified = basicInfo.getThumbLastModified().getTime();
			imgName = basicInfo.getImgName();

			albumLastModifImg = commURIFormatter.format(
					new Object[]{albumName, thumbLastModified, imgName});

			basicInfo.setThumbPath(imageURIFormatter.format(
					new Object[]{thumbsDir, albumLastModifImg}));
			basicInfo.setImagePath(imageURIFormatter.format(
					new Object[]{albumsDir, albumLastModifImg}));
		}
	}

	public void appendImageDimensions(List<? extends ImageDimensions> imageDimensions) {
		for (ImageDimensions row : imageDimensions) {
			if (row.getImageHeight() < row.getImageWidth()) {
				row.setImageHeight((int)
						Math.floor(maxThumbSize * row.getImageHeight() / row.getImageWidth()));
				row.setImageWidth(maxThumbSizeInt);
			} else {
				row.setImageWidth((int)
						Math.floor(maxThumbSize * row.getImageWidth() / row.getImageHeight()));
				row.setImageHeight(maxThumbSizeInt);
			}
		}
	}

}
