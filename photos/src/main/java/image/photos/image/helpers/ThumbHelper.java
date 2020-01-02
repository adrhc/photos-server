package image.photos.image.helpers;

import image.photos.infrastructure.filestore.FileStoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Date;

/**
 * Created by adr on 2/10/18.
 */
@Component
public class ThumbHelper {
	private final FileStoreService fileStoreService;
	@Value("${thumbs.dir}")
	private String thumbsDir;
	@Value("${albums.dir}")
	private String albumsDir;

	public ThumbHelper(FileStoreService fileStoreService) {this.fileStoreService = fileStoreService;}

	public Date thumbLastModified(Path imageFile, Date defaultValue) {
		Path thumbFile = thumbFileForImgFile(imageFile);
		if (this.fileStoreService.exists(thumbFile)) {
			return new Date(this.fileStoreService.lastModifiedTime(thumbFile));
		} else {
			return defaultValue;
		}
	}

	public Path thumbFileForImgFile(Path imageFile) {
		return Path.of(imageFile.toString().replaceFirst(this.albumsDir, this.thumbsDir));
	}
}
