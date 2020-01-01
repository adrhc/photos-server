package image.photos.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import static image.photos.util.PathUtils.lastModifiedTime;

/**
 * Created by adr on 2/10/18.
 */
@Component
public class ThumbUtils {
	@Value("${thumbs.dir}")
	private String thumbsDir;
	@Value("${albums.dir}")
	private String albumsDir;

	public Date getThumbLastModified(Path imageFile, Date defaultValue) {
		Path thumbFile = getThumbFileForImgFile(imageFile);
		if (Files.exists(thumbFile)) {
			return new Date(lastModifiedTime(thumbFile));
		} else {
			return defaultValue;
		}
	}

	public Path getThumbFileForImgFile(Path imageFile) {
		return Path.of(imageFile.toString().replaceFirst(this.albumsDir, this.thumbsDir));
	}
}
