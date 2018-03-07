package image.persistence.repository.junit4.staging.image;

import image.persistence.entity.Album;
import image.persistence.entity.IAlbumSupplier;
import image.persistence.entity.IImageSupplier;
import image.persistence.entity.Image;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.ImageRepository;
import image.persistence.repository.springtestconfig.HbmStagingJdbcDsConfig;
import image.persistence.repository.util.ITransactionalAction;
import net.jcip.annotations.NotThreadSafe;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Created by adr on 2/27/18.
 */
@RunWith(SpringRunner.class)
@NotThreadSafe
@HbmStagingJdbcDsConfig
@Category(HbmStagingJdbcDsConfig.class)
public class ImageRepoCreateTest implements IImageSupplier, IAlbumSupplier, ITransactionalAction {
	protected static final Logger logger = LoggerFactory.getLogger(ImageRepoCreateTest.class);

	@Inject
	protected AlbumRepository albumRepository;
	@Inject
	protected ImageRepository imageRepository;

	protected Album album;

	@Before
	public void setUp() {
		doTransaction(() -> {
			this.album = this.albumRepository.createAlbum(supplyAlbumName());
			logger.debug("album.id = {}, album.name = {}", this.album.getId(), this.album.getName());
		});
	}

	@Test
	public void persistImage() throws Exception {
		Image image = supplyImage(this.album);
		this.imageRepository.persistImage(image);
		Assert.assertNotNull(image.getId());
		logger.debug("image.id = {}, image.name = {}", image.getId(), image.getName());
	}

	@After
	public void removeAlbumAndImage() {
		this.albumRepository.deleteAlbum(this.album.getId());
		logger.debug("removing album.id = {}, album.name = {}",
				this.album.getId(), this.album.getName());
	}
}
