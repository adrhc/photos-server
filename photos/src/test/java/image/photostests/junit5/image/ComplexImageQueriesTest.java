package image.photostests.junit5.image;

import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2x.repositories.ImageRepository;
import image.jpa2xtests.repositories.ImageTestBase;
import image.persistence.entity.Image;
import image.persistence.entitytests.assertion.IImageAssertions;
import image.photos.infrastructure.database.ImageQueryRepositoryEx;
import image.photos.infrastructure.mixed.ComplexImageQueries;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import image.photostests.overrides.infrastructure.filestore.FileStoreServiceTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(properties = "hibernate.show_sql=false")
@Junit5PhotosInMemoryDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
@Slf4j
class ComplexImageQueriesTest extends ImageTestBase implements IImageAssertions {
	@Autowired
	private ComplexImageQueries complexImageQueries;
	@Autowired
	private ImageQueryRepositoryEx imageQueryRepositoryEx;
	@Autowired
	private FileStoreServiceTest fileStoreService;
	@Autowired
	private ImageRepository imageRepository;

	@Test
	void getImages() {
		List<Image> dbImages = this.imageRepository.findByAlbumId(this.album.getId());
		this.album.getImages().forEach(i -> {
			this.assertImageEquals(i, dbImages.stream()
					.filter(dbi -> dbi.getId().equals(i.getId()))
					.findAny().orElseThrow(AssertionError::new));
		});
	}

	@Test
	void findByNameAndAlbumId() {
		Image image = this.album.getImages().get(0);
		Image dbImage = this.imageQueryRepositoryEx
				.findByNameAndAlbumId(image.getName(), this.album.getId());
		Assertions.assertNotNull(dbImage);
		this.assertImageEquals(image, dbImage);
	}

	@Test
	void imageExistsInOtherAlbum() throws IOException {
		Image image = this.album.getImages().get(0);
		Path imgFile = Path.of(image.getName());
		// found in another album because its album is declared
		// to be "album.id - 1" instead of the real one (album.id)
		boolean exists = this.complexImageQueries.imageExistsInOtherAlbum(imgFile, this.album.getId() - 1);
		assertTrue(exists);
	}

	@Test
	void noOtherAlbumHasIt() throws IOException {
		Image image = this.album.getImages().get(0);
		Path imgFile = Path.of(image.getName());
		// image found only in its album; the declared album is the real one
		boolean exists = this.complexImageQueries.imageExistsInOtherAlbum(imgFile, this.album.getId());
		assertFalse(exists);
	}

	@Test
	void sizeDifference() throws IOException {
		Image image = this.album.getImages().get(1);
		Path imgFile = Path.of(image.getName());
		this.fileStoreService.addSize1Path(imgFile);
		// not found because of the size difference
		boolean exists = this.complexImageQueries.imageExistsInOtherAlbum(imgFile, this.album.getId() - 1);
		assertFalse(exists);
	}

	@Test
	void shortName() throws IOException {
		Image image = this.album.getImages().get(0);
		Path imgFile = Path.of(image.getName().substring(0, image.getName().length() - 2));
		// shorter name
		boolean exists = this.complexImageQueries.imageExistsInOtherAlbum(imgFile, this.album.getId() - 1);
		assertTrue(exists);
	}

	@Test
	void longName() throws IOException {
		Image image = this.album.getImages().get(0);
		Path imgFile = Path.of(image.getName());
		// longer name
		boolean exists = this.complexImageQueries.imageExistsInOtherAlbum(imgFile, this.album.getId() - 1);
		assertTrue(exists);
	}
}
