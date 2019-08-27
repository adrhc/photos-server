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

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestPropertySource(properties = "hibernate.show_sql=false")
@Junit5PhotosInMemoryDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
@Slf4j
class ExifExtractorServiceTest {
	private static final String IMAGE = "/home/adr/Pictures/" +
			"FOTO Daniela & Adrian jpeg/albums/2017-10-14 Family/20171105_130105.jpg";
	@Autowired
	private ExifExtractorService service;

	@Test
	void extractMetadata() {
		ImageMetadata imageMetadata = this.service.extractMetadata(new File(IMAGE));
		assertNotNull(imageMetadata);
	}
}