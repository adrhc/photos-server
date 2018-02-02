package image.exifweb.image;

import org.apache.commons.lang.text.StrBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by adr on 2/2/18.
 */
@Component
public class ImageUtils {
	@Value("${thumbs.dir}")
	private String thumbsDir;
	@Value("${albums.dir}")
	private String albumsDir;
	@Value("${max.thumb.size}")
	private double maxThumbSize;
	@Value("${max.thumb.size}")
	private int maxThumbSizeInt;

	public void appendImageURIs(List<? extends ImageThumb> thumbs) {
		StrBuilder thumbPath = new StrBuilder(64);
		StrBuilder imagePath = new StrBuilder(64);
		for (ImageThumb thumb : thumbs) {
			if (thumb.getImgName() != null) {
				// 'thumbs'/albumName/thumbLastModified/imgName
				thumbPath.append(thumbsDir).append('/');
				imagePath.append(albumsDir).append('/');

				Stream.of(thumbPath, imagePath).forEach(sb -> sb
						.append(thumb.getAlbumName())
						.append('/')
						.append(thumb.getThumbLastModified().getTime())
						.append('/')
						.append(thumb.getImgName())
				);

				thumb.setThumbPath(thumbPath.toString());
				thumb.setImagePath(imagePath.toString());

				thumbPath.clear();
				imagePath.clear();
			}
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
