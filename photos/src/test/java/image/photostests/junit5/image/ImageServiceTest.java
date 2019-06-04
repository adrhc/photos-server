package image.photostests.junit5.image;

import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2x.repositories.AlbumRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entitytests.assertion.IImageAssertions;
import image.photos.image.ImageService;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import io.github.glytching.junit.extension.random.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Junit5PhotosInMemoryDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
@Slf4j
class ImageServiceTest implements IImageAssertions {
	private static final int IMAGE_COUNT = 5;
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private ImageService imageService;

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
	void findByNameAndAlbumId() {
		Image image = this.album.getImages().get(0);
		Optional<Image> dbImageOpt =
				this.imageService.findByNameAndAlbumId(image.getName(), this.album.getId());
		Assertions.assertTrue(dbImageOpt.isPresent());
		dbImageOpt.ifPresent(i -> {
			assertImageEquals(image, i);
		});
	}

	@AfterAll
	void tearDown() {
		this.albumRepository.deleteById(this.album.getId());
	}
}
