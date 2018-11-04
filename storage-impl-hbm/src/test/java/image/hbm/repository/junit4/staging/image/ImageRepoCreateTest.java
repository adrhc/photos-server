package image.hbm.repository.junit4.staging.image;

import image.hbm.repository.springconfig.HbmStagingJdbcDbConfig;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entitytests.IAlbumSupplier;
import image.persistence.entitytests.IImageSupplier;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.ImageRepository;
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
@HbmStagingJdbcDbConfig
@Category(HbmStagingJdbcDbConfig.class)
public class ImageRepoCreateTest implements IImageSupplier, IAlbumSupplier {
	protected static final Logger logger = LoggerFactory.getLogger(ImageRepoCreateTest.class);

	@Inject
	protected AlbumRepository albumRepository;
	@Inject
	protected ImageRepository imageRepository;
	protected Album album;

	@Before
	public void setUp() {
		this.album = this.albumRepository.createByName(supplyAlbumName());
		logger.debug("album.id = {}, album.name = {}", this.album.getId(), this.album.getName());
	}

	@Test
	public void persistImage() throws Exception {
		Image image = supplyImage(this.album);
		this.imageRepository.persist(image);
		Assert.assertNotNull(image.getId());
		logger.debug("image.id = {}, image.name = {}", image.getId(), image.getName());
	}

	@After
	public void removeAlbumAndImage() {
		this.albumRepository.deleteById(this.album.getId());
	}
}