package image.photostests.junit4.album;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import image.cdm.album.page.AlbumPage;
import image.photos.JsonMapperConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static exifweb.util.file.ClassPathUtils.pathOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * In order not to use SpringRunner we'd have to use SpringClassRule @ClassRule and SpringMethodRule @Rule.
 * <p>
 * Created by adr on 2/21/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = JsonMapperConfig.class)
@Category(JsonMapperConfig.class)
public class AlbumPageJsonTest {
	/**
	 * 2013-04-20 Simfonia lalelelor
	 * <p>
	 * contains only default visible pictures (not hidden, personal, deleted, etc)
	 */
	private static final Path asc1Json = pathOf("classpath:json/10/asc1.json");
	@Autowired
	private ObjectMapper mapper;

	@Test
	public void decodeAlbumPagesJson() throws IOException {
		List<AlbumPage> albumPages =
				this.mapper.readValue(asc1Json.toFile(), new TypeReference<>() {});
		assertThat(albumPages, hasSize(10));
	}
}
