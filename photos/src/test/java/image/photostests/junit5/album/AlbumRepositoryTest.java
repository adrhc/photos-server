package image.photostests.junit5.album;

import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2x.repositories.album.AlbumRepository;
import image.jpa2x.repositories.image.ImageRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entitytests.assertion.IImageAssertions;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import io.github.glytching.junit.extension.random.Random;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.spi.CacheImplementor;
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

@TestPropertySource(properties = "hibernate.show_sql=false")
@Junit5PhotosInMemoryDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
@Slf4j
class AlbumRepositoryTest implements IImageAssertions {
	private static final int IMAGE_COUNT = 5;
	@PersistenceContext
	protected EntityManager em;
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private ImageRepository imageRepository;

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

	/**
	 * valid only when not using @Cache on Album.images
	 */
	@Test
	void albumImagesNoCacheTest() {
		AlbumImagesCacheData data = this.preAlbumImagesCacheTest();
		assertFalse(data.cache.containsCollection(data.albumImagesRegionName, this.album.getId()),
				"Album[" + this.album.getId() + "].images found in cache!");
	}

	@Test
	@Disabled("valid only when using @Cache on Album.images")
	void albumImagesCacheTest() {
		AlbumImagesCacheData data = this.preAlbumImagesCacheTest();
		assertTrue(data.cache.containsCollection(data.albumImagesRegionName, this.album.getId()),
				"Album[" + this.album.getId() + "].images not in cache!");
	}

	private AlbumImagesCacheData preAlbumImagesCacheTest() {
		// it's about cache testing for *images* collection from Album
		// see @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE) on Album.getImages()
		String albumImagesRegionName = Album.class.getName().concat(".images");
		org.hibernate.Cache cache = this.em.getEntityManagerFactory()
				.getCache().unwrap(CacheImplementor.class);
		cache.evictCollectionData(albumImagesRegionName, this.album.getId());
		assertFalse(cache.containsCollection(albumImagesRegionName, this.album.getId()),
				"Album[" + this.album.getId() + "].images already in cache!");
		this.imageRepository.findByAlbumId(this.album.getId());
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
