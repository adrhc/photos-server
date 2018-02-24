package image.persistence.repository.albumrepository;

import image.persistence.HibernateConfig;
import image.persistence.entity.Album;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.springtestconfig.ISpringClassRuleSupport;
import image.persistence.repository.springtestconfig.InMemoryDbTestConfig;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adr on 2/23/18.
 */
@InMemoryDbTestConfig
@Category(HibernateConfig.class)
public class AlbumWriteTest implements ISpringClassRuleSupport {
	private static final Logger logger = LoggerFactory.getLogger(AlbumRepositoryTest.class);

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

	@Inject
	private AlbumRepository albumRepository;

	@Test
	public void createAlbum() throws Exception {
		Album album = albumRepository.createAlbum("album " + sdf.format(new Date()));
		Assert.assertNotNull(album.getId());
		logger.debug("album.id = {}", album.getId());
	}

	@Ignore("todo: use in memory database")
	@Test
	public void putAlbumCover() throws Exception {
		boolean result = albumRepository.putAlbumCover(1);
		Assert.assertTrue(result);
	}

	@Ignore("todo: use in memory database")
	@Test
	public void removeAlbumCover() throws Exception {
		boolean result = albumRepository.removeAlbumCover(1);
		Assert.assertTrue(result);
	}

	@Ignore("todo: use in memory database")
	@Test
	public void clearDirtyForAlbum() throws Exception {
		boolean result = albumRepository.clearDirtyForAlbum(1);
		Assert.assertTrue(result);
	}
}
