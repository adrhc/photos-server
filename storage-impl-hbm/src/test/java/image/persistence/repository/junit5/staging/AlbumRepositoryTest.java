package image.persistence.repository.junit5.staging;

import image.persistence.entity.Album;
import image.persistence.entity.IAlbumSupplier;
import image.persistence.entity.IImageSupplier;
import image.persistence.entity.Image;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.junit5.springconfig.Junit5HbmStagingJdbcDbConfig;
import image.persistence.repository.junit5.springconfig.Junit5HbmStagingJdbcDbNestedConfig;
import image.persistence.util.assertion.IAlbumAssertions;
import image.persistence.util.assertion.IImageAssertions;
import image.persistence.util.random.RandomBeansExtensionEx;
import io.github.glytching.junit.extension.random.Random;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RandomBeansExtensionEx.class)
@Junit5HbmStagingJdbcDbConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlbumRepositoryTest implements IAlbumSupplier, IImageSupplier, IAlbumAssertions, IImageAssertions {
	private static final Logger logger = LoggerFactory.getLogger(AlbumRepositoryTest.class);

	@Autowired
	private AlbumRepository albumRepository;

	@Random(type = Album.class, size = 30, excludes = {"id", "dirty", "images", "cover", "lastUpdate"})
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
	void getAlbumsOrderedByName() {
		List<String> descSortedNames = notDeletedAlbumNamesDesc(this.albums);
		List<Album> dbAlbums = this.albumRepository.findByDeletedFalseOrderByNameDesc();
		List<String> descDbSortedNames = notDeletedAlbumNames(dbAlbums);
		descDbSortedNames.retainAll(descSortedNames);
		assertThat("size", descDbSortedNames, hasSize(descSortedNames.size()));
		assertThat("same order", descDbSortedNames, equalTo(descSortedNames));
	}

	@Test
	void deleteAlbum() {
		Integer albumId = this.albums.remove(this.albums.size() - 1).getId();
		this.albumRepository.deleteById(albumId);
		Album removedAlbum = this.albumRepository.getById(albumId);
		assertNull(removedAlbum);
	}

	@Test
	void getAlbumById() {
		Album album = this.albums.get(0);
		Album dbAlbum = this.albumRepository.getById(album.getId());
		assertAlbumEquals(album, dbAlbum);
	}

	@Test
	void findAlbumByName() {
		Album album = this.albums.get(0);
		Album dbAlbum = this.albumRepository.findAlbumByName(album.getName());
		assertAlbumEquals(album, dbAlbum);
	}

	@Test
	void clearDirtyForAlbum() {
		Album album = this.albums.get(0);
		this.albumRepository.clearDirtyForAlbum(album.getId());
		Album dbAlbum = this.albumRepository.getById(album.getId());
		assertFalse(dbAlbum.isDirty());
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

	@Junit5HbmStagingJdbcDbNestedConfig
	class CreateAlbumTest extends AlbumCreationTestBase {
		@BeforeAll
		void beforeAll(@Random(excludes = {"id", "images", "cover", "lastUpdate"}) Album album) {
			this.album = album;
		}

		@Test
		void createAlbum() {
			this.albumRepository.persist(this.album);
			Album dbAlbum = this.albumRepository.getById(this.album.getId());
			assertAlbumEquals(this.album, dbAlbum);
		}
	}

	@Junit5HbmStagingJdbcDbNestedConfig
	class CreateAlbumForNameTest extends AlbumCreationTestBase {
		@Test
		void createAlbumForName(@Random String albumName) {
			this.album = this.albumRepository.createByName(albumName);
			assertEquals(albumName, this.album.getName());
		}
	}

	abstract class CoverTestBase {
		@Autowired
		AlbumRepository albumRepository;

		/**
		 * @Random on field won't work in abstract class!
		 */
		Album album;

		@BeforeAll
		void givenAlbum(@Random(excludes = {"id", "deleted", "images", "cover", "lastUpdate"})
				                Album album,
		                @Random(type = Image.class, excludes = {"id", "lastUpdate", "album"})
				                List<Image> images) {
			this.album = album;
			this.album.addImages(images);
			this.albumRepository.persist(this.album);
		}

		@AfterAll
		void afterAll() {
			this.albumRepository.deleteById(this.album.getId());
		}
	}

	@Junit5HbmStagingJdbcDbNestedConfig
	class PutAlbumCoverTest extends CoverTestBase {
		@Test
		void putAlbumCover() {
			Image cover = this.album.getImages().get(0);
			this.albumRepository.putAlbumCover(cover.getId());
			Album dbAlbum = this.albumRepository.getById(this.album.getId());
			assertImageEquals(cover, dbAlbum.getCover());
		}
	}

	@Junit5HbmStagingJdbcDbNestedConfig
	class RemoveAlbumCoverTest extends CoverTestBase {
		@Override
		@BeforeAll
		void givenAlbum(@Random(excludes = {"id", "deleted", "images", "cover", "lastUpdate"})
				                Album album,
		                @Random(type = Image.class, excludes = {"id", "lastUpdate", "album"})
				                List<Image> images) {
			album.setCover(images.get(0));
			super.givenAlbum(album, images);
		}

		@Test
		void removeAlbumCover() {
			this.albumRepository.removeAlbumCover(this.album.getId());
			Album dbAlbum = this.albumRepository.getById(this.album.getId());
			assertNull(dbAlbum.getCover());
		}
	}
}
