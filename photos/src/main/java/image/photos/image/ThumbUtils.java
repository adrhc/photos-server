package image.photos.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

/**
 * Created by adr on 2/10/18.
 */
@Component
public class ThumbUtils {
	@Value("${thumbs.dir}")
	private String thumbsDir;
	@Value("${albums.dir}")
	private String albumsDir;

	public Date getThumbLastModified(File imageFile, Date defaultValue) {
		File thumbFile = getThumbFileForImgFile(imageFile);
		if (thumbFile.exists()) {
			return new Date(thumbFile.lastModified());
		} else {
			return defaultValue;
		}
	}

	public File getThumbFileForImgFile(File imageFile) {
		return new File(imageFile.getPath().replaceFirst(albumsDir, thumbsDir));
	}
}
