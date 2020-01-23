package image.photostests.junit5.image;

import exifweb.util.random.RandomBeansExtensionEx;
import image.jpa2xtests.repositories.ImageTestBase;
import image.photos.image.helpers.ImageHelper;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(properties = "hibernate.show_sql=false")
@Junit5PhotosInMemoryDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
@Slf4j
class ImageHelperTest extends ImageTestBase {
	@Autowired
	private ImageHelper imageHelper;

	@Test
	void changeToOppositeExtensionCase() {
		assertEquals("x.Y", this.imageHelper.changeToOppositeExtensionCase("x.y"));
		assertEquals(".Y", this.imageHelper.changeToOppositeExtensionCase(".y"));
		assertEquals("x", this.imageHelper.changeToOppositeExtensionCase("x"));
	}
}
