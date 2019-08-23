package image.jpa2xtests.repositories;

import exifweb.util.random.IPositiveIntegerRandom;
import exifweb.util.random.RandomBeansExtensionEx;
import image.cdm.image.ImageRating;
import image.cdm.image.status.EImageStatus;
import image.cdm.image.status.ImageStatus;
import image.jpa2x.repositories.ImageRepository;
import image.jpa2xtests.config.Junit5Jpa2xInMemoryDbConfig;
import image.persistence.entity.Image;
import image.persistence.entity.image.IImageFlagsUtils;
import image.persistence.entity.image.ImageMetadata;
import image.persistence.entitytests.assertion.IImageAssertions;
import io.github.glytching.junit.extension.random.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = "hibernate.show_sql=true")
@ExtendWith(RandomBeansExtensionEx.class)
@Junit5Jpa2xInMemoryDbConfig
@Slf4j
class ImageRepositoryTest extends ImageTestBase implements IImageAssertions, IPositiveIntegerRandom, IImageFlagsUtils {
	@PersistenceContext
	protected EntityManager em;
	@Autowired
	private ImageRepository imageRepository;

	@Test
	void persistImage(@Random(excludes = {"id", "lastUpdate", "album"}) Image image) {
		this.album.addImage(image);
		log.debug("*** imageRepository.persist(image) ***");
		this.imageRepository.persist(image);
		log.debug("*** imageRepository.findById ***");
		Image dbImage = this.imageRepository.findById(image.getId()).orElseThrow(AssertionError::new);
		assertImageEquals(image, dbImage);
	}

	/**
	 * https://stackoverflow.com/questions/26242492/how-to-cache-results-of-a-spring-data-jpa-query-method-without-using-query-cache/
	 * <p>
	 * check sql logs
	 */
	@Test
	void checkQueryCache() {
		Image image = this.album.getImages().get(0);

		log.debug("*** imageRepository.findById ***");
		this.em.getEntityManagerFactory().getCache().evictAll();
		this.imageRepository.findById(image.getId());
		this.imageRepository.findById(image.getId());

		log.debug("*** imageRepository.count ***");
		this.em.getEntityManagerFactory().getCache().evictAll();
		this.imageRepository.count();
		this.imageRepository.count();

		log.debug("*** imageRepository.findAll ***");
		this.em.getEntityManagerFactory().getCache().evictAll();
		this.imageRepository.findAll();
		this.imageRepository.findAll();

		log.debug("*** imageRepository.findByAlbumId ***");
		this.em.getEntityManagerFactory().getCache().evictAll();
		this.imageRepository.findByAlbumId(this.album.getId());
		this.imageRepository.findByAlbumId(this.album.getId());
	}

	@Test
	void findByNameAndAlbumId() {
		Image image = this.album.getImages().get(0);
		Image dbImage = this.imageRepository.findByNameAndAlbumId(image.getName(), this.album.getId());
		assertImageEquals(image, dbImage);
	}

	@Test
	void findDuplicates() {
		Image image = this.album.getImages().get(0);
		List<Image> dbImages = this.imageRepository
				.findDuplicates(
						image.getName().replaceFirst("[.][^.]+$", ""),
						image.getImageMetadata().getExifData().getDateTimeOriginal(),
						image.getAlbum().getId() - 1);
		assertFalse(dbImages.isEmpty());
		assertImageEquals(image, dbImages.get(0));
	}

	@Test
	void findByAlbumId() {
		List<Image> dbImages = this.imageRepository.findByAlbumId(this.album.getId());
		this.album.getImages().forEach(img -> {
			Optional<Image> dbImgOpt = dbImages.stream()
					.filter(i -> i.getId().equals(img.getId()))
					.findAny();
			assertTrue(dbImgOpt.isPresent());
			assertImageEquals(img, dbImgOpt.get());
		});
	}

	@Test
	void updateThumbLastModifiedForImg() {
		Image image = this.album.getImages().get(0);
		Date date = new Date();
		log.debug("*** imageRepository.updateThumbLastModifiedForImg ***");
		this.imageRepository.updateThumbLastModifiedForImg(date, image.getId());
		log.debug("*** imageRepository.findById ***");
		Image dbImage = this.imageRepository.findById(image.getId()).orElseThrow(AssertionError::new);
		assertEquals(date, dbImage.getImageMetadata().getThumbLastModified());
		image.getImageMetadata().setThumbLastModified(date);
		assertImageEquals(image, dbImage);
	}

	@Test
	void changeRating() {
		Image image = this.album.getImages().get(0);
		ImageRating imageRating = new ImageRating(image.getId(),
				(byte) (1 + randomPositiveInt(5)));
		log.debug("*** imageRepository.changeRating ***");
		this.imageRepository.changeRating(imageRating);
		log.debug("*** imageRepository.findById ***");
		Image dbImage = this.imageRepository.findById(image.getId()).orElseThrow(AssertionError::new);
		assertEquals(imageRating.getRating(), dbImage.getRating());
		image.setRating(imageRating.getRating());
		assertImageEquals(image, dbImage);
	}

	@Test
	void changeStatus(@Random EImageStatus status) {
		Image image = this.album.getImages().get(0);
		ImageStatus imageStatus = new ImageStatus(image.getId(), status.getValueAsByte());
		log.debug("*** imageRepository.changeStatus ***");
		this.imageRepository.changeStatus(imageStatus);
		log.debug("*** imageRepository.findById ***");
		Image dbImage = this.imageRepository.findById(image.getId()).orElseThrow(AssertionError::new);
		assertEquals(of(imageStatus.getStatus()), dbImage.getFlags());
		image.setFlags(of(imageStatus.getStatus()));
		assertImageEquals(image, dbImage);
	}

	@Test
	void markDeleted() {
		Image image = this.album.getImages().get(0);
		log.debug("*** imageRepository.markDeleted ***");
		this.imageRepository.markDeleted(image.getId());
		log.debug("*** imageRepository.findById ***");
		Image dbImage = this.imageRepository.findById(image.getId()).orElseThrow(AssertionError::new);
		assertTrue(dbImage.isDeleted());
		image.setDeleted(true);
		assertImageEquals(image, dbImage);
	}

	@Test
	void changeName(@Random String newName) {
		Image image = this.album.getImages().get(0);
		this.imageRepository.changeName(newName, image.getId());
		Image dbImage = this.imageRepository.findById(image.getId()).orElseThrow(AssertionError::new);
		assertEquals(newName, dbImage.getName());
		image.setName(newName);
		assertImageEquals(image, dbImage);
	}

	@Test
	void updateImageMetadata(@Random ImageMetadata imageMetadata) {
		Image image = this.album.getImages().get(0);
		this.imageRepository.updateImageMetadata(imageMetadata, image.getId());
		Image dbImage = this.imageRepository.findById(image.getId()).orElseThrow(AssertionError::new);
		assertImageMetadataEquals(imageMetadata, dbImage.getImageMetadata());
		image.setImageMetadata(imageMetadata);
		assertImageEquals(image, dbImage);
	}
}
