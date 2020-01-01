package image.photos.album.importing;

import image.jpa2x.repositories.AlbumRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.function.Predicate;

@Component
@Slf4j
public class Predicates {
	public Predicate<File> VALID_ALBUM_PATH = path -> {
		// cazul in care albumPath este o poza
		if (path.isFile()) {
			log.error("Wrong albumPath (is a file):\n{}", path.getPath());
			return false;
		}
		// valid album
		return true;
	};
	@Autowired
	private AlbumRepository albumRepository;
	public Predicate<File> VALID_NEW_ALBUM_PATH = this.VALID_ALBUM_PATH
			.and(path -> {
				// check for path to have files
				File[] albumFiles = path.listFiles();
				if (albumFiles == null || albumFiles.length == 0) {
					// ne dorim sa fie album nou dar albumPath nu are poze asa ca daca
					// ar fi intr-adevar album nou atunci nu ar avea sens sa-l import
					log.warn("{} este gol!", path.getPath());
					return false;
				}
				// check path for not to already be an album
				return this.albumRepository.findByName(path.getName()) == null;
			});
}
