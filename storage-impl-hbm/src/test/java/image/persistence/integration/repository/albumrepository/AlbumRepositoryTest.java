package image.persistence.integration.repository.albumrepository;

import image.persistence.HibernateConfig;
import image.persistence.entity.Album;
import image.persistence.integration.repository.AlbumRepository;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasItem;

/**
 * Created by adr on 2/23/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {HibernateConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=false")
@ActiveProfiles({"jdbc-ds"})
@Category(HibernateConfig.class)
public class AlbumRepositoryTest {
	private static final Logger logger = LoggerFactory.getLogger(AlbumRepositoryTest.class);

	@Inject
	private AlbumRepository albumRepository;

	@Test
	public void getAlbumsOrderedByName() throws Exception {
		List<Album> albums = albumRepository.getAlbumsOrderedByName();
		assertThat(albums, hasItem(anything()));
		logger.debug("albums.size = {}", albums.size());
	}

	@Test
	public void getAlbumById() throws Exception {
		Album album = albumRepository.getAlbumById(1);
		Assert.assertNotNull(album);
		logger.debug("albums:\n{}", album.toString());
	}

	@Test
	public void getAlbumByName() throws Exception {
		Album album = albumRepository.getAlbumByName("2011-08-19-Gradac");
		Assert.assertNotNull(album);
		logger.debug("albums:\n{}", album.toString());
	}
}
