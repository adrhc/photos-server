package image.persistence.integration.repository.albumrepository;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;
import image.persistence.HibernateConfig;
import image.persistence.entity.Album;
import image.persistence.integration.repository.AlbumRepository;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adr on 2/23/18.
 */
@RunWith(JExample.class)
@ContextConfiguration(classes = {HibernateConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=false")
@ActiveProfiles({"jdbc-ds"})
@Category(HibernateConfig.class)
public class AlbumWriteTest {
	private static final Logger logger = LoggerFactory.getLogger(AlbumRepositoryTest.class);

	@ClassRule
	public static final SpringClassRule springClassRule = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

	@Inject
	private AlbumRepository albumRepository;

	@Ignore("todo: use in memory database")
	@Test
	public void createAlbum() throws Exception {
		Album album = albumRepository.createAlbum("album " + sdf.format(new Date()));
		Assert.assertNotNull(album.getId());
		logger.debug("album.id = {}", album.getId());
	}

	@Ignore("todo: use in memory database")
	@Given("#createAlbum")
	@Test
	public void putAlbumCover() throws Exception {
		boolean result = albumRepository.putAlbumCover(1);
		Assert.assertTrue(result);
	}

	@Ignore("todo: use in memory database")
	@Given("#putAlbumCover")
	@Test
	public void removeAlbumCover() throws Exception {
		boolean result = albumRepository.removeAlbumCover(1);
		Assert.assertTrue(result);
	}

	@Ignore("todo: use in memory database")
	@Given("#removeAlbumCover")
	@Test
	public void clearDirtyForAlbum() throws Exception {
		boolean result = albumRepository.clearDirtyForAlbum(1);
		Assert.assertTrue(result);
	}
}
