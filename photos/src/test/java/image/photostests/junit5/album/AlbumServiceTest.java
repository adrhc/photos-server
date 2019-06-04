package image.photostests.junit5.album;

import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2x.repositories.AlbumRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entitytests.assertion.IImageAssertions;
import image.photos.album.AlbumService;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import io.github.glytching.junit.extension.random.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@TestPropertySource(properties = "hibernate.show_sql=false")
@Junit5PhotosInMemoryDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
@Slf4j
class AlbumServiceTest implements IImageAssertions {
	private static final int IMAGE_COUNT = 5;
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
	void setUp(@Random(type = Image.class, size = IMAGE_COUNT,
			excludes = {"id", "lastUpdate", "album"})
			List<Image> images) {
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

	@AfterAll
	void tearDown() {
		this.albumRepository.deleteById(this.album.getId());
	}
}
