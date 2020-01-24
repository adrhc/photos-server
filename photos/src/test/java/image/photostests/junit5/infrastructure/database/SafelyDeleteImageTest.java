package image.photostests.junit5.infrastructure.database;

import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2x.repositories.image.ImageRepository;
import image.jpa2xtests.repositories.ImageTestBase;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entitytests.assertion.IAlbumAssertions;
import image.persistence.entitytests.assertion.IImageAssertions;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import io.github.glytching.junit.extension.random.Random;
import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = "hibernate.show_sql=false")
@ExtendWith(RandomBeansExtensionEx.class)
@Junit5PhotosInMemoryDbConfig
@NotThreadSafe// because of album.addImage(image)
@Slf4j
class SafelyDeleteImageTest extends ImageTestBase implements IAlbumAssertions, IImageAssertions {
	@Autowired
	private ImageRepository imageRepository;

	@Test
	void safelyDeleteImage(@Random(excludes = {"id", "lastUpdate", "album"}) Image image) {
		log.debug("persistImage");
		image.setDeleted(false);
		image.setAlbum(this.album);
		this.imageRepository.persist(image);
		log.debug("albumRepository.putAlbumCover");
		this.albumRepository.putAlbumCover(image.getId());
		log.debug("imageUpdateRepositoryEx.safelyDeleteImage");
		this.imageRepository.safelyDeleteImage(image.getId());
		log.debug("imageRepository.findById");
		Optional<Image> dbImage = this.imageRepository.findById(image.getId());
		assertFalse(dbImage.isPresent());
		log.debug("albumRepository.findById");
		Album dbAlbum = this.albumRepository.getById(this.album.getId());
		assertNull(dbAlbum.getCover());
		List<Image> dbImages = this.imageRepository.findByAlbumId(this.album.getId());
		this.assertImagesEquals(this.album.getImages(), dbImages);
	}

	@Test
	void safelyDeleteAMarkedAsDeletedImage(@Random(
			excludes = {"id", "lastUpdate", "album"}) Image image) {
		log.debug("persistImage");
		image.setDeleted(true);
		this.album.addImage(image);
		this.imageRepository.persist(image);
		log.debug("albumRepository.putAlbumCover");
		this.albumRepository.putAlbumCover(image.getId());
		log.debug("imageUpdateRepositoryEx.safelyDeleteImage");
		this.imageRepository.safelyDeleteImage(image.getId());
		log.debug("imageRepository.findById");
		Optional<Image> dbImage = this.imageRepository.findById(image.getId());
		assertTrue(dbImage.isPresent());
		assertTrue(dbImage.get().isDeleted());
		log.debug("albumRepository.findById");
		Album dbAlbum = this.albumRepository.getById(this.album.getId());
		assertNull(dbAlbum.getCover());
		List<Image> dbImages = this.imageRepository.findByAlbumId(this.album.getId());
		this.assertImagesEquals(this.album.getImages(), dbImages);
	}
}
