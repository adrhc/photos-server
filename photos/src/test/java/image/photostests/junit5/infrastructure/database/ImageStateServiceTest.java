package image.photostests.junit5.infrastructure.database;

import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2x.repositories.ImageRepository;
import image.jpa2xtests.repositories.ImageTestBase;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;
import image.persistence.entitytests.assertion.IImageAssertions;
import image.photos.infrastructure.database.ImageStateService;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import io.github.glytching.junit.extension.random.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(properties = "hibernate.show_sql=true")
@ExtendWith(RandomBeansExtensionEx.class)
@Junit5PhotosInMemoryDbConfig
@Slf4j
public class ImageStateServiceTest extends ImageTestBase implements IImageAssertions {
	@Autowired
	private ImageStateService imageStateService;
	@Autowired
	private ImageRepository imageRepository;

	@Test
	void updateThumbLastModifiedForImg() {
		Image image = this.album.getImages().get(0);
		Date date = new Date();
		log.debug("*** imageRepository.updateThumbLastModifiedForImg ***");
		this.imageStateService.updateThumbLastModified(date, image.getId());
		log.debug("*** imageRepository.findById ***");
		Image dbImage = this.imageRepository.findById(image.getId()).orElseThrow(AssertionError::new);
		assertEquals(date, dbImage.getImageMetadata().getThumbLastModified());
		image.getImageMetadata().setThumbLastModified(date);
		this.assertImageEquals(image, dbImage);
	}

	@Test
	void markDeleted() {
		Image image = this.album.getImages().get(0);
		log.debug("*** imageRepository.markDeleted ***");
		this.imageStateService.markDeleted(image.getId());
		log.debug("*** imageRepository.findById ***");
		Image dbImage = this.imageRepository.findById(image.getId()).orElseThrow(AssertionError::new);
		assertTrue(dbImage.isDeleted());
		image.setDeleted(true);
		this.assertImageEquals(image, dbImage);
	}

	@Test
	void changeName(@Random String newName) {
		Image image = this.album.getImages().get(0);
		this.imageStateService.changeName(newName, image.getId());
		Image dbImage = this.imageRepository.findById(image.getId()).orElseThrow(AssertionError::new);
		assertEquals(newName, dbImage.getName());
		image.setName(newName);
		this.assertImageEquals(image, dbImage);
	}

	@Test
	void updateImageMetadata(@Random ImageMetadata imageMetadata) {
		Image image = this.album.getImages().get(0);
		this.imageStateService.updateImageMetadata(imageMetadata, image.getId());
		Image dbImage = this.imageRepository.findById(image.getId()).orElseThrow(AssertionError::new);
		this.assertImageMetadataEquals(imageMetadata, dbImage.getImageMetadata());
		image.setImageMetadata(imageMetadata);
		this.assertImageEquals(image, dbImage);
	}
}
