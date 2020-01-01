package image.photostests.junit5.image;

import exifweb.util.random.RandomBeansExtensionEx;
import image.persistence.entity.image.ImageMetadata;
import image.photos.image.ExifExtractorService;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestPropertySource(properties = "hibernate.show_sql=false")
@Junit5PhotosInMemoryDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
@Slf4j
class ExifExtractorServiceTest {
	private static final String IMAGE = "classpath:20171105_130105.jpg";
	@Autowired
	private ExifExtractorService service;

	@Test
	void extractMetadata() throws FileNotFoundException {
		Path imagePath = Path.of(ResourceUtils.getFile(IMAGE).getAbsolutePath());
		log.debug("path:\n{}", imagePath);
		ImageMetadata imageMetadata = this.service.extractMetadata(imagePath);
		assertNotNull(imageMetadata);
		assertNotNull(imageMetadata);
		assertNotNull(imageMetadata.getExifData());
		assertNotNull(imageMetadata.getDateTime());
		assertNotNull(imageMetadata.getThumbLastModified());
		assertNotNull(imageMetadata.getExifData().getDateTimeOriginal());
		assertNotNull(imageMetadata.getExifData().getShutterSpeedValue());
		assertEquals(imageMetadata.getExifData().getShutterSpeedValue(), "1/17 sec");
	}
}
