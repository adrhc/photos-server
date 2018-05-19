package image.persistence.repository.junit5.staging;

import image.cdm.image.ImageRating;
import image.cdm.image.status.EImageStatus;
import image.cdm.image.status.ImageStatus;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.ImageRepository;
import image.persistence.repository.junit5.springconfig.Junit5HbmStagingJdbcDbConfig;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RandomBeansExtensionEx.class)
@NotThreadSafe
@Junit5HbmStagingJdbcDbConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageRepositoryTest implements IImageAssertions, IPositiveIntegerRandom {
	@Inject
	private AlbumRepository albumRepository;
	@Inject
	private ImageRepository imageRepository;

	@Random(excludes = {"id", "lastUpdate", "cover", "images"})
	private Album album;

	/**
	 * Notice that ImageMetadata is generated too and will be used in tests!
	 */
	@BeforeAll
	void setUp(@Random(type = Image.class, size = 30, excludes = {"id", "lastUpdate", "album"})
			           List<Image> images) {
		// hibernate might proxy images collection so better
		// just copy images instead of directly using it
		this.album.addImages(images);
		this.albumRepository.createAlbum(this.album);
	}

	@AfterAll
	void tearDown() {
		this.albumRepository.deleteAlbumById(this.album.getId());
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
		List<Image> dbImages = this.imageRepository.getImagesByAlbumId(this.album.getId());
		this.album.getImages().forEach(img -> {
			List<Image> dbImgs = dbImages.stream()
					.filter(i -> i.getId().equals(img.getId()))
					.collect(Collectors.toList());
			assertEquals(1, dbImgs.size());
			assertImageEquals(img, dbImgs.get(0));
		});
	}

	@Test
	void persistImage(@Random(excludes = {"id", "lastUpdate", "album"}) Image image) {
		this.album.addImage(image);
		this.imageRepository.persistImage(image);
		Image dbImage = this.imageRepository.getImageById(image.getId());
		assertImageEquals(image, dbImage);
	}

	@Test
	void markDeleted() {
		Image image = pickRandomlyAnImage();
		this.imageRepository.markDeleted(image.getId());
		Image dbImage = this.imageRepository.getImageById(image.getId());
		assertTrue(dbImage.isDeleted());
		image.setDeleted(true);
		assertImageEquals(image, dbImage);
	}

	@Test
	void deleteImage() {
		Image image = this.album.getImages().remove(this.album.getImages().size() - 1);
		this.imageRepository.deleteImage(image.getId());
		Image dbImage = this.imageRepository.getImageById(image.getId());
		assertNull(dbImage);
	}

	@Test
	void safelyDeleteImage() {
		Image image = this.album.getImages().remove(this.album.getImages().size() - 1);
		this.albumRepository.putAlbumCover(image.getId());
		this.imageRepository.safelyDeleteImage(image.getId());
		Image dbImage = this.imageRepository.getImageById(image.getId());
		assertNull(dbImage);
		Album dbAlbum = this.albumRepository.getAlbumById(this.album.getId());
		assertNull(dbAlbum.getCover());
	}

	@Test
	void changeName(@Random String newName) {
		Image image = pickRandomlyAnImage();
		this.imageRepository.changeName(newName, image.getId());
		Image dbImage = this.imageRepository.getImageById(image.getId());
		assertEquals(newName, dbImage.getName());
		image.setName(newName);
		assertImageEquals(image, dbImage);
	}

	@Test
	void updateImageMetadata(@Random ImageMetadata imageMetadata) {
		Image image = pickRandomlyAnImage();
		this.imageRepository.updateImageMetadata(imageMetadata, image.getId());
		Image dbImage = this.imageRepository.getImageById(image.getId());
		assertImageMetadataEquals(imageMetadata, dbImage.getImageMetadata());
		image.setImageMetadata(imageMetadata);
		assertImageEquals(image, dbImage);
	}

	@Test
	void getImageByNameAndAlbumId() {
		Image image = pickRandomlyAnImage();
		Image dbImage = this.imageRepository.getImageByNameAndAlbumId(
				image.getName(), this.album.getId());
		assertImageEquals(image, dbImage);
	}

	@Test
	void getImageById() {
		Image image = pickRandomlyAnImage();
		Image dbImage = this.imageRepository.getImageById(image.getId());
		assertImageEquals(image, dbImage);
	}

	private Image pickRandomlyAnImage() {
		List<Image> images = this.album.getImages();
		return images.get(randomPositiveInt(images.size()));
	}
}
