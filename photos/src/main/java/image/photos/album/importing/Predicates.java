package image.photos.album.importing;

import image.jpa2x.repositories.AlbumRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

import static image.photos.album.AlbumUtils.albumName;
import static image.photos.album.AlbumUtils.emptyAlbum;

@Component
@Slf4j
public class Predicates {
	public Predicate<Path> VALID_ALBUM_PATH = path -> {
		// cazul in care albumPath este o poza
		if (!Files.isDirectory(path)) {
			log.error("Wrong albumPath (is a file):\n{}", path);
			return false;
		}
		// valid album
		return true;
	};
	@Autowired
	private AlbumRepository albumRepository;
	public Predicate<Path> VALID_NEW_ALBUM_PATH = this.VALID_ALBUM_PATH
			.and(path -> {
				// check for path to have files
				if (emptyAlbum(path)) {
					// ne dorim sa fie album nou dar albumPath nu are poze asa ca daca
					// ar fi intr-adevar album nou atunci nu ar avea sens sa-l import
					log.warn("{} este gol!", path);
					return false;
				}
				// check path for not to already be an album
				return this.albumRepository.findByName(albumName(path)) == null;
			});
}
