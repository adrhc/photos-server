package image.persistence.repository.junit5;

import image.cdm.image.ImageRating;
import image.cdm.image.status.EImageStatus;
import image.cdm.image.status.ImageStatus;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.ImageRepository;
import image.persistence.repository.junit5.testconfig.Junit5HbmStagingJdbcDbConfig;
import image.persistence.repository.util.assertion.IImageAssertions;
import image.persistence.repository.util.random.RandomBeansExtensionEx;
import image.persistence.util.IPositiveIntegerRandom;
import io.github.glytching.junit.extension.random.Random;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RandomBeansExtensionEx.class)
@NotThreadSafe
@Junit5HbmStagingJdbcDbConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageRepositoryTest implements IImageAssertions, IPositiveIntegerRandom {
	@Inject
	private AlbumRepository albumRepository;
	@Inject
	private ImageRepository imageRepository;

	@Random(excludes = {"id", "lastUpdate", "cover"})
	private Album album;
	/**
	 * Notice that ImageMetadata is generated too and will be used in tests!
	 */
	@Random(type = Image.class, size = 30, excludes = {"id", "lastUpdate"})
	private List<Image> images;

	@BeforeAll
	void setUp() {
		this.album.setImages(this.images);
		this.albumRepository.createAlbum(this.album);
	}

	@AfterAll
	void tearDown() {
		this.albumRepository.deleteAlbum(this.album.getId());
	}

	private Image pickRandomlyAnImage() {
		return this.images.get(randomPositiveInt(this.images.size()));
	}

	@Test
	void updateThumbLastModifiedForImg() {
		Image image = pickRandomlyAnImage();
		Date date = new Date();
		this.imageRepository.updateThumbLastModifiedForImg(date, image.getId());
		Image dbImage = this.imageRepository.getImageById(image.getId());
		assertEquals(date, dbImage.getImageMetadata().getThumbLastModified());
		image.getImageMetadata().setThumbLastModified(date);
		assertImageEquals(image, dbImage);
	}

	@Test
	void changeRating() {
		Image image = pickRandomlyAnImage();
		ImageRating imageRating = new ImageRating(image.getId(),
				(byte) (1 + randomPositiveInt(5)));
		this.imageRepository.changeRating(imageRating);
		Image dbImage = this.imageRepository.getImageById(image.getId());
		assertEquals(imageRating.getRating(), dbImage.getRating());
		image.setRating(imageRating.getRating());
		assertImageEquals(image, dbImage);
	}

	@Test
	void changeStatus(@Random EImageStatus status) {
		Image image = pickRandomlyAnImage();
		ImageStatus imageStatus = new ImageStatus(image.getId(), status.getValueAsByte());
		this.imageRepository.changeStatus(imageStatus);
		Image dbImage = this.imageRepository.getImageById(image.getId());
		assertEquals(imageStatus.getStatus(), dbImage.getStatus());
		image.setStatus(imageStatus.getStatus());
		assertImageEquals(image, dbImage);
	}

	@Test
	void getImagesByAlbumId() {
	}

	@Test
	void persistImage() {
	}

	@Test
	void markDeleted() {
	}

	@Test
	void deleteImage() {
	}

	@Test
	void safelyDeleteImage() {
	}

	@Test
	void changeName() {
	}

	@Test
	void updateImageMetadata() {
	}

	@Test
	void getImageByNameAndAlbumId() {
	}

	@Test
	void getImageById() {
	}
}