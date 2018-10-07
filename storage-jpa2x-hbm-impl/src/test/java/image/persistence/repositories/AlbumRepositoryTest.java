package image.persistence.repositories;

import image.persistence.config.Junit5Jpa2xInMemoryDbConfig;
import image.persistence.entity.Album;
import image.persistence.repository.util.assertion.IAlbumAssertions;
import image.persistence.repository.util.random.RandomBeansExtensionEx;
import io.github.glytching.junit.extension.random.Random;
import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RandomBeansExtensionEx.class)
@NotThreadSafe
@Junit5Jpa2xInMemoryDbConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class AlbumRepositoryTest implements IAlbumAssertions {
	@Autowired
	private AlbumRepository albumRepository;

	@Random(type = Album.class, size = 50, excludes = {"id", "dirty", "images", "cover", "lastUpdate"})
	private List<Album> albums;

	@BeforeAll
	void givenAlbums() {
		this.albums.forEach(this.albumRepository::persist);
	}

	@AfterAll
	void afterAll() {
		this.albums.forEach(a -> this.albumRepository.deleteById(a.getId()));
	}

	@Test
	void getAlbumById() {
		Album album = this.albums.get(0);
		Album dbAlbum = this.albumRepository.getById(album.getId());
		assertAlbumEquals(album, dbAlbum);
	}

	@Test
	void getAlbumByName() {
		Album album = this.albums.get(0);
		Album dbAlbum = this.albumRepository.findAlbumByName(album.getName());
		assertAlbumEquals(album, dbAlbum);
	}

	@Nested
	@Junit5Jpa2xInMemoryDbConfig
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class CreateAlbumForNameTest extends AlbumCreationTestBase {
		@Test
		void createAlbumForName(@Random String albumName) {
			this.album = this.albumRepository.createByName(albumName);
			assertEquals(albumName, this.album.getName());
		}
	}

	abstract class AlbumCreationTestBase {
		@Autowired
		AlbumRepository albumRepository;

		/**
		 * @Random on field won't work in abstract class!
		 */
		Album album;

		@AfterAll
		void afterAll() {
			this.albumRepository.deleteById(this.album.getId());
		}
	}
}
