package image.jpa2xtests.repositories;

import image.jpa2x.repositories.AlbumRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entitytests.assertion.IAlbumAssertions;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

public abstract class AlbumTestBase implements IAlbumAssertions {
	@PersistenceContext
	protected EntityManager em;
	@Autowired
	protected AlbumRepository albumRepository;
	List<Album> albums;
	Date before = new Date();

	@BeforeAll
	void givenAlbums(
			@Random(type = Album.class, size = 25, excludes = {"id", "dirty", "images", "cover", "lastUpdate"})
					List<Album> albums,
			@Random(type = Image.class, size = 5,
					excludes = {"id", "lastUpdate", "album"}) List<Image> images
	) {
		this.albums = albums;
		this.albums.get(0).addImages(images);
		this.albums.forEach(this.albumRepository::persist);
	}

	@AfterAll
	void afterAll() {
		this.albums.forEach(a -> this.albumRepository.deleteById(a.getId()));
	}
}
