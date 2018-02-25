package image.persistence.repository.albumrepository;

import image.persistence.HibernateConfig;
import image.persistence.entity.Album;
import image.persistence.entity.IAlbumSupplier;
import image.persistence.entity.IImageSupplier;
import image.persistence.entity.Image;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.ImageRepository;
import image.persistence.repository.springtestconfig.InMemoryDbTestConfig;
import image.persistence.repository.springtestconfig.SpringRunnerRulesBased;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by adr on 2/23/18.
 */
@NotThreadSafe
@InMemoryDbTestConfig
@Category(HibernateConfig.class)
public class AlbumWriteTest extends SpringRunnerRulesBased
		implements IImageSupplier, IAlbumSupplier {
	private static final Logger logger = LoggerFactory.getLogger(AlbumRepositoryTest.class);


	@Inject
	private AlbumRepository albumRepository;
	@Inject
	private ImageRepository imageRepository;

	@Before
	@Transactional
	public void createAnAlbum() throws Exception {
		Album album = albumRepository.createAlbum(supplyAlbumName());
		assertThat(album.getId(), equalTo(1));
		logger.debug("album.id = {}", album.getId());
		Image image = supplyImage(album);
		imageRepository.persistImage(image);
		assertThat(image.getId(), equalTo(1));
		logger.debug("image.id = {}", image.getId());
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
