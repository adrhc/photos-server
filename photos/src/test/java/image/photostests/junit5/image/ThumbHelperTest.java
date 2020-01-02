package image.photostests.junit5.image;

import exifweb.util.random.RandomBeansExtensionEx;
import image.photos.image.helpers.ThumbHelper;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import image.photostests.overrides.infrastructure.filestore.FileStoreServiceTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

import java.nio.file.Path;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@TestPropertySource(properties = "hibernate.show_sql=true")
@Junit5PhotosInMemoryDbConfig
@ExtendWith(RandomBeansExtensionEx.class)
@Slf4j
class ThumbHelperTest {
	@Autowired
	private ThumbHelper thumbHelper;
	@Autowired
	private FileStoreServiceTest fileStoreService;
	@Value("${thumbs.dir}")
	private String thumbsDir;
	@Value("${albums.dir}")
	private String albumsDir;

	@Test
	void getThumbLastModified() {
		Path thumbFile = Path.of(this.thumbsDir, "image.jpeg");
		this.fileStoreService.setSpecialLastModifiedTimeForPath(thumbFile);
		Date date = this.thumbHelper.getThumbLastModified(
				Path.of(this.albumsDir, "image.jpeg"), new Date());
		assertThat(date, is(new Date(FileStoreServiceTest.specialLastModifiedTime)));
	}

	@Test
	void getThumbFileForImgFile() {
		Path path = this.thumbHelper.getThumbFileForImgFile(
				Path.of(this.albumsDir, "image.jpeg"));
		assertThat(path.getParent().toString(), is(this.thumbsDir));
	}
}