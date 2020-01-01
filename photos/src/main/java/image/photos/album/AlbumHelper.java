package image.photos.album;

import image.jpa2x.repositories.AppConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class AlbumHelper {
	@Autowired
	private AppConfigRepository appConfigRepository;

	public Path rootPath() {
		return Path.of(this.appConfigRepository.getAlbumsPath());
	}

	public Path fullPath(String albumName) {
		return rootPath().resolve(albumName);
	}
}
