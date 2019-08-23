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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestPropertySource(properties = "hibernate.show_sql=true")
@Junit5PhotosInMemoryDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
@Slf4j
class ImageUtilsTest extends ImageTestBase {
	@Autowired
	private ImageUtils imageUtils;

	@Test
	void imageExistsInOtherAlbum() {
		Image image = this.album.getImages().get(0);
		File imgFile = Mockito.mock(File.class);
		Mockito.when(imgFile.getName()).thenReturn(image.getName());
		Mockito.when(imgFile.length()).thenReturn(0L);
		// found in another album because its album is declared to be "album.id - 1" instead of the real one (album.id)
		boolean exists = this.imageUtils.imageExistsInOtherAlbum(imgFile,
				image.getImageMetadata().getExifData().getDateTimeOriginal(),
				this.album.getId() - 1);
		assertTrue(exists);
		// not found in another album; the declared album is the real one
		exists = this.imageUtils.imageExistsInOtherAlbum(imgFile,
				image.getImageMetadata().getExifData().getDateTimeOriginal(),
				this.album.getId());
		assertFalse(exists);
		// not found because of the size difference
		Mockito.when(imgFile.length()).thenReturn(1L);
		exists = this.imageUtils.imageExistsInOtherAlbum(imgFile,
				image.getImageMetadata().getExifData().getDateTimeOriginal(),
				this.album.getId() - 1);
		assertFalse(exists);
	}
}
