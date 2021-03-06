package image.jpa2xtests.repositories;

import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2x.repositories.album.AlbumRepository;
import image.jpa2xtests.config.Junit5Jpa2xInMemoryDbConfig;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entitytests.assertion.IImageAssertions;
import io.github.glytching.junit.extension.random.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Cache;
import java.util.Date;
import java.util.List;

import static org.exparity.hamcrest.date.DateMatchers.sameOrAfter;
import static org.exparity.hamcrest.date.DateMatchers.sameOrBefore;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RandomBeansExtensionEx.class)
@Junit5Jpa2xInMemoryDbConfig
@Slf4j
class AlbumRepositoryTest extends AlbumTestBase {
	@Test
	void getAlbumById() {
		Album album = this.albums.get(0);
		Album dbAlbum = this.albumRepository.getById(album.getId());
		this.assertAlbumEquals(album, dbAlbum);
	}

	@Test
	void findAlbumByName() {
		Album album = this.albums.get(0);
		Album dbAlbum = this.albumRepository.findByName(album.getName());
		this.assertAlbumEquals(album, dbAlbum);
	}

	@Test
	void albumByIdCacheTest() {
		Album album = this.albums.get(0);
		Cache cache = this.em.getEntityManagerFactory().getCache();
		cache.evict(Album.class, album.getId());
		assertFalse(cache.contains(Album.class, album.getId()), "Album:" + album.getId() + " already in cache!");
		this.albumRepository.findById(album.getId());
		assertTrue(cache.contains(Album.class, album.getId()), "Album:" + album.getId() + " not in cache!");
	}

	@Test
	void albumByNameCacheTest() {
		Album album = this.albums.get(0);
		Cache cache = this.em.getEntityManagerFactory().getCache();
		cache.evict(Album.class, album.getId());
		assertFalse(cache.contains(Album.class, album.getId()), "Album:" + album.getId() + " already in cache!");
		this.albumRepository.findByName(album.getName());
		assertTrue(cache.contains(Album.class, album.getId()), "Album:" + album.getId() + " not in cache!");
	}

	@Test
	void clearDirtyForAlbum() {
		Album album = this.albums.get(0);
		this.albumRepository.clearDirty(album.getId());
		Album dbAlbum = this.albumRepository.getById(album.getId());
		assertFalse(dbAlbum.isDirty());
	}

	@Test
	void getAlbumCoversLastUpdateDate() {
		Date date = this.albumRepository.getMaxLastUpdateForAll();
		assertThat(date, both(sameOrAfter(this.before))
				.and(sameOrBefore(new Date())));
	}

	@Junit5Jpa2xInMemoryDbConfig
	@Nested
	class CreateAlbumTest extends AlbumCreationTestBase {
		@BeforeAll
		void beforeAll(@Random(excludes = {"id", "images", "cover", "lastUpdate"}) Album album) {
			this.album = album;
		}

		@Test
		void createAlbum() {
			this.albumRepository.persist(this.album);
			Album dbAlbum = this.albumRepository.getById(this.album.getId());
			AlbumRepositoryTest.this.assertAlbumEquals(this.album, dbAlbum);
		}
	}

	@Junit5Jpa2xInMemoryDbConfig
	@Nested
	class CreateAlbumForNameTest extends AlbumCreationTestBase {
		@Test
		void createAlbumForName(@Random String albumName) {
			this.album = new Album(albumName);
			this.albumRepository.persist(this.album);
			assertEquals(albumName, this.album.getName());
		}
	}

	@Junit5Jpa2xInMemoryDbConfig
	@Nested
	class PutAlbumCoverTest extends CoverTestBase implements IImageAssertions {
		@Test
		void putAlbumCover() {
			Image cover = this.album.getImages().get(0);
			this.albumRepository.putAlbumCover(cover.getId());
			Album dbAlbum = this.albumRepository.getById(this.album.getId());
			this.assertImageEquals(cover, dbAlbum.getCover());
		}
	}

	@Junit5Jpa2xInMemoryDbConfig
	@Nested
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
