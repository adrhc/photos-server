package image.persistence.repository.junit5;

import image.persistence.entity.Album;
import image.persistence.entity.IAlbumSupplier;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.junit5.testconfig.Junit5HbmStagingJdbcDbConfig;
import image.persistence.repository.util.assertion.IAlbumAssertions;
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
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(RandomBeansExtensionEx.class)
@NotThreadSafe
@Junit5HbmStagingJdbcDbConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlbumRepositoryTest implements IAlbumSupplier, IAlbumAssertions {
	private static final Logger logger = LoggerFactory.getLogger(AlbumRepositoryTest.class);

	@Autowired
	private AlbumRepository albumRepository;

	@Random(type = Album.class, size = 30, excludes = {"id", "images", "cover", "lastUpdate"})
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
		List<String> descSortedNames = this.albums.stream()
				.filter(a -> !a.isDeleted())
				.map(Album::getName)
				.sorted((o1, o2) -> o2.toLowerCase().compareTo(o1.toLowerCase()))
				.collect(Collectors.toList());
		List<Album> dbAlbums = this.albumRepository.getAlbumsOrderedByName();
		assertThat("size", dbAlbums, hasSize(descSortedNames.size()));
		for (Album dbAlbum : dbAlbums) {
			assertEquals(descSortedNames.remove(0), dbAlbum.getName(), "sorting");
		}
	}

	@Test
	void createAlbum(@Random(excludes = {"id", "images", "cover", "lastUpdate"}) Album album) {
		this.albumRepository.createAlbum(album);
		Album dbAlbum = this.albumRepository.getAlbumById(album.getId());
		assertAlbumEquals(album, dbAlbum);
	}

	@Test
	void createAlbumByName(@Random String albumName) {
		Album dbAlbum = this.albumRepository.createAlbum(albumName);
		assertEquals(albumName, dbAlbum.getName());
	}

	@Test
	void deleteAlbum() {
		Integer albumId = this.albums.remove(this.albums.size() - 1).getId();
		this.albumRepository.deleteAlbum(albumId);
		Album removedAlbum = this.albumRepository.getAlbumById(albumId);
		assertNull(removedAlbum);
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