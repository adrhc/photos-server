package image.persistence.repository.junit5;

import image.persistence.entity.Album;
import image.persistence.entity.IAlbumSupplier;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.junit5.testconfig.Junit5HbmStagingJdbcDbConfig;
import image.persistence.repository.util.random.RandomBeansExtensionEx;
import io.github.glytching.junit.extension.random.Random;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(RandomBeansExtensionEx.class)
@NotThreadSafe
@Junit5HbmStagingJdbcDbConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlbumRepositoryTest implements IAlbumSupplier {
	private static final Logger logger = LoggerFactory.getLogger(AlbumRepositoryTest.class);

	@Autowired
	private AlbumRepository albumRepository;

	@Random(type = Album.class, size = 15, excludes = {"id", "images", "cover", "lastUpdate"})
	private List<Album> albums;

	@BeforeAll
	void beforeAll() {
		this.albums.forEach(this.albumRepository::createAlbum);
	}

	@AfterAll
	void afterAll() {
		this.albums.forEach(a -> this.albumRepository.deleteAlbum(a.getId()));
	}

	@Test
	void getAlbumsOrderedByName() {
		List<Album> albums = this.albumRepository.getAlbumsOrderedByName();
		assertThat(albums, hasSize((int) this.albums
				.stream().filter(a -> !a.isDeleted()).count()));
	}

	@Test
	void createAlbum() {
	}

	@Test
	void createAlbum1() {
	}

	@Test
	void deleteAlbum() {
	}

	@Test
	void getAlbumById() {
	}

	@Test
	void getAlbumByName() {
	}

	@Test
	void putAlbumCover() {
	}

	@Test
	void removeAlbumCover() {
	}

	@Test
	void clearDirtyForAlbum() {
	}
}