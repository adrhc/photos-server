package image.photos.junit4.album;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import image.cdm.album.page.AlbumPage;
import image.photos.JsonMapperConfig;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * In order not to use SpringRunner we'll have to use SpringClassRule @ClassRule and SpringMethodRule @Rule.
 * <p>
 * Created by adr on 2/21/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = JsonMapperConfig.class)
@Category(JsonMapperConfig.class)
public class AlbumPageJsonTest {
    /**
     * jsonFile contains only default visible pictures (not hidden, personal, deleted, etc)
     */
    private static final Integer IMAGE_COUNT = 10;
    /**
     * 2013-04-20 Simfonia lalelelor
     */
    private final File jsonFile =
            new File("/home/adr/apps/opt/apache-htdocs/photos/json/10/asc1.json");
    @Inject
    private ObjectMapper mapper;

    @Before
    public void before() {
        Assume.assumeTrue(jsonFile.exists());
    }

    @Test
    public void decodeAlbumPagesJson() throws IOException {
        List<AlbumPage> albumPages =
                this.mapper.readValue(jsonFile, new TypeReference<List<AlbumPage>>() {});
        assertThat(albumPages, hasSize(IMAGE_COUNT));
    }
}
