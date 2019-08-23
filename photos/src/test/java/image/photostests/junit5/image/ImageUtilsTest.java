package image.photostests.junit5.image;

import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2xtests.repositories.ImageTestBase;
import image.persistence.entity.Image;
import image.photos.image.ImageUtils;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;


@TestPropertySource(properties = "hibernate.show_sql=true")
@Junit5PhotosInMemoryDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
@Slf4j
class ImageUtilsTest extends ImageTestBase {
	@Autowired
	private ImageUtils imageUtils;

	@Test
	void changeToOppositeExtensionCase() {
		assertEquals("x.Y", this.imageUtils.changeToOppositeExtensionCase("x.y"));
		assertEquals(".Y", this.imageUtils.changeToOppositeExtensionCase(".y"));
		assertEquals("x", this.imageUtils.changeToOppositeExtensionCase("x"));
	}

	@Test
	void imageExistsInOtherAlbum() {
		Image image = this.album.getImages().get(0);
		File imgFile = Mockito.mock(File.class);
		Mockito.when(imgFile.getName()).thenReturn(image.getName());
		Mockito.when(imgFile.length()).thenReturn(0L);
		// found in another album because its album is declared to be "album.id - 1" instead of the real one (album.id)
		boolean exists = this.imageUtils.imageExistsInOtherAlbum(imgFile, this.album.getId() - 1);
		assertTrue(exists);
	}

	@Test
	void noOtherAlbumHasIt() {
		Image image = this.album.getImages().get(0);
		File imgFile = fileMock(0L, image.getName());
		// image found only in its album; the declared album is the real one
		boolean exists = this.imageUtils.imageExistsInOtherAlbum(imgFile, this.album.getId());
		assertFalse(exists);
	}

	@Test
	void sizeDifference() {
		Image image = this.album.getImages().get(0);
		File imgFile = fileMock(1L, image.getName());
		// not found because of the size difference
		boolean exists = this.imageUtils.imageExistsInOtherAlbum(imgFile, this.album.getId() - 1);
		assertFalse(exists);
	}

	@Test
	void shortName() {
		Image image = this.album.getImages().get(0);
		File imgFile = fileMock(0L, image.getName().substring(0, image.getName().length() - 2));
		// shorter name
		boolean exists = this.imageUtils.imageExistsInOtherAlbum(imgFile, this.album.getId() - 1);
		assertTrue(exists);
	}

	@Test
	void longName() {
		Image image = this.album.getImages().get(0);
		File imgFile = fileMock(0L, image.getName() + "x");
		// longer name
		boolean exists = this.imageUtils.imageExistsInOtherAlbum(imgFile, this.album.getId() - 1);
		assertTrue(exists);
	}

	private File fileMock(long length, String name) {
		File imgFile = Mockito.mock(File.class);
		Mockito.when(imgFile.getName()).thenReturn(name);
		Mockito.when(imgFile.length()).thenReturn(length);
		return imgFile;
	}
}
