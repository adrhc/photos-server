package image.photostests.junit5.image;

import exifweb.util.random.RandomBeansExtensionEx;
import image.persistence.entity.image.ExifData;
import image.persistence.entity.image.ImageMetadata;
import image.photos.image.services.ExifExtractorService;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static exifweb.util.file.ClassPathUtils.pathOf;
import static image.persistence.entity.util.DateUtils.safeFormat;
import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = "hibernate.show_sql=false")
@Junit5PhotosInMemoryDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
@Slf4j
class ExifExtractorServiceTest {
	private static final DateTimeFormatter sdf =
			DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").withZone(ZoneOffset.UTC);
	private static final String IMAGE = "classpath:images/20171105_130105.jpg";
	@Autowired
	private ExifExtractorService service;

	@Test
	void extractMetadata() throws FileNotFoundException {
		Path imagePath = pathOf(IMAGE);
		log.debug("path:\n{}", imagePath);
		ImageMetadata imageMetadata = this.service.extractMetadata(imagePath);
		assertNotNull(imageMetadata);
		assertNotNull(imageMetadata);
		ExifData exifData = imageMetadata.getExifData();
		assertNotNull(exifData);
		assertNotNull(imageMetadata.getDateTime());
		assertNotNull(imageMetadata.getThumbLastModified());
		assertNotNull(exifData.getDateTimeOriginal());
		assertEquals(safeFormat(exifData.getDateTimeOriginal(), sdf), "2017.11.05 13:01:05");
		assertNotEquals(exifData.getDateTimeOriginal(), imageMetadata.getDateTime());
		assertNotNull(exifData.getShutterSpeedValue());
		assertEquals(exifData.getShutterSpeedValue(), "1/17 sec");
	}
}
