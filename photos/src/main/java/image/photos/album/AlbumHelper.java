package image.photos.album;

import image.jpa2x.repositories.AppConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class AlbumHelper {
	@Autowired
	private AppConfigRepository appConfigRepository;

	public File rootPath() {
		return new File(this.appConfigRepository.getAlbumsPath());
	}

	public File fullPath(String albumName) {
		return new File(this.appConfigRepository.getAlbumsPath(), albumName);
	}
}
