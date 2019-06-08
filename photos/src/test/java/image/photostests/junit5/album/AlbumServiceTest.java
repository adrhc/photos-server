package image.photostests.junit5.album;

import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2x.repositories.AlbumRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entitytests.assertion.IImageAssertions;
import image.photos.album.AlbumService;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import io.github.glytching.junit.extension.random.Random;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.CacheImplementor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(properties = "hibernate.show_sql=true")
@Junit5PhotosInMemoryDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
@Slf4j
class AlbumServiceTest implements IImageAssertions {
	private static final int IMAGE_COUNT = 5;
	@PersistenceContext
	protected EntityManager em;
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private AlbumService albumService;

	@Random(excludes = {"id", "lastUpdate", "cover", "images"})
	private Album album;

	/**
	 * Notice that ImageMetadata is generated too and will be used in tests!
	 */
	@BeforeAll
	void setUp(
			@Random(type = Image.class, size = IMAGE_COUNT,
					excludes = {"id", "lastUpdate", "album"})
					List<Image> images
	) {
		this.album.addImages(images);
		this.albumRepository.save(this.album);
	}

	@Test
	void getImages() {
		List<Image> dbImages = this.albumService.getImages(this.album.getId());
		this.album.getImages().forEach(i -> {
			assertImageEquals(i, dbImages.stream()
					.filter(dbi -> dbi.getId().equals(i.getId()))
					.findAny().orElseThrow(AssertionError::new));
		});
	}

	/**
	 * valid only when not using @Cache on Album.images
	 */
	@Test
	void albumImagesNoCacheTest() {
		AlbumImagesCacheData data = preAlbumImagesCacheTest();
		assertFalse(data.cache.containsCollection(data.albumImagesRegionName, this.album.getId()),
				"Album[" + this.album.getId() + "].images found in cache!");
	}

	/**
	 * valid only when using @Cache on Album.images
	 */
	@Test
	@Disabled
	void albumImagesCacheTest() {
		AlbumImagesCacheData data = preAlbumImagesCacheTest();
		assertTrue(data.cache.containsCollection(data.albumImagesRegionName, this.album.getId()),
				"Album[" + this.album.getId() + "].images not in cache!");
	}

	private AlbumImagesCacheData preAlbumImagesCacheTest() {
		String albumImagesRegionName = Album.class.getName().concat(".images");
		org.hibernate.Cache cache = this.em.getEntityManagerFactory()
				.getCache().unwrap(CacheImplementor.class);
		cache.evictCollection(albumImagesRegionName, this.album.getId());
		assertFalse(cache.containsCollection(albumImagesRegionName, this.album.getId()),
				"Album[" + this.album.getId() + "].images already in cache!");
		this.albumService.getImages(this.album.getId());
		return new AlbumImagesCacheData(cache, albumImagesRegionName);
	}

	@AfterAll
	void tearDown() {
		this.albumRepository.deleteById(this.album.getId());
	}

	@AllArgsConstructor
	private class AlbumImagesCacheData {
		org.hibernate.Cache cache;
		String albumImagesRegionName;
	}
}
