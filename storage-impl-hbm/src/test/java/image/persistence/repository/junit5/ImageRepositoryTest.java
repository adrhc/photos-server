package image.persistence.repository.junit5;

import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.ImageRepository;
import image.persistence.repository.junit5.testconfig.Junit5HbmStagingJdbcDbConfig;
import image.persistence.repository.util.random.RandomBeansExtensionEx;
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
class ImageRepositoryTest {
	@Inject
	private AlbumRepository albumRepository;
	@Inject
	private ImageRepository imageRepository;

	@Random(excludes = {"id", "lastUpdate", "cover"})
	private Album album;
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

	@Test
	void updateThumbLastModifiedForImg() {
		Image image = this.images.get(0);
		Date date = new Date();
		// image.imageMetadata is already created & filled with data by @Random
		image.getImageMetadata().setThumbLastModified(new Date());
		this.imageRepository.updateThumbLastModifiedForImg(date, image.getId());
		Image dbImage = this.imageRepository.getImageById(image.getId());
		assertEquals(date, dbImage.getImageMetadata().getThumbLastModified());
	}

	@Test
	void changeRating() {
	}

	@Test
	void changeStatus() {
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