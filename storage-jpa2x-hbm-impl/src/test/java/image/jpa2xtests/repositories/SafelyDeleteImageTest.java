package image.jpa2xtests.repositories;

import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.ImageRepository;
import image.jpa2xtests.config.Junit5Jpa2xInMemoryDbConfig;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entitytests.assertion.IAlbumAssertions;
import image.persistence.entitytests.assertion.IImageAssertions;
import io.github.glytching.junit.extension.random.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = "hibernate.show_sql=true")
@ExtendWith(RandomBeansExtensionEx.class)
@Junit5Jpa2xInMemoryDbConfig
@Slf4j
class SafelyDeleteImageTest implements IAlbumAssertions, IImageAssertions {
	private Album album;
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private ImageRepository imageRepository;

	/**
	 * Notice that ImageMetadata is generated too and will be used in tests!
	 */
	@BeforeAll
	void setUp(
			@Random(excludes = {"id", "lastUpdate", "cover", "images"})
					Album album,
			@Random(type = Image.class, excludes = {"id", "lastUpdate", "album"})
					List<Image> images
	) {
		this.album = album;
		this.album.addImages(images);
		this.albumRepository.save(this.album);
	}

	@AfterAll
	void tearDown() {
		this.albumRepository.deleteById(this.album.getId());
	}

	@Test
	void safelyDeleteImage() {
		// sync in memory album with subsequent image db deletion
		Image image = this.album.getImages().remove(this.album.getImages().size() - 1);
		log.debug("*** albumRepository.putAlbumCover ***");
		this.albumRepository.putAlbumCover(image.getId());
		log.debug("*** imageRepository.safelyDeleteImage ***");
		this.imageRepository.safelyDeleteImage(image.getId());
		log.debug("*** imageRepository.findById ***");
		Optional<Image> dbImage = this.imageRepository.findById(image.getId());
		assertFalse(dbImage.isPresent());
		log.debug("*** albumRepository.findById ***");
		Album dbAlbum = this.albumRepository.getById(this.album.getId());
		assertNull(dbAlbum.getCover());
		assertEquals(this.album.getId(), dbAlbum.getId());
		assertEquals(this.album.getName(), dbAlbum.getName());
		List<Image> dbImages = this.imageRepository.findByAlbumId(this.album.getId());
		assertImagesEquals(this.album.getImages(), dbImages);
	}
}
