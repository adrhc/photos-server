package image.persistence.repository.albumrepository;

import image.persistence.entity.Album;
import image.persistence.entity.IAlbumSupplier;
import image.persistence.entity.IImageSupplier;
import image.persistence.entity.Image;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.ImageRepository;
import image.persistence.repository.springtestconfig.springrunner.SpringRunnerRulesBased;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by adr on 2/25/18.
 */
public abstract class AlbumRepoWriteTestBase extends SpringRunnerRulesBased
		implements IImageSupplier, IAlbumSupplier {
	protected static final Logger logger = LoggerFactory.getLogger(AlbumRepositoryTest.class);

	@Inject
	protected AlbumRepository albumRepository;
	@Inject
	protected ImageRepository imageRepository;

//	@Before
//	public void setUp() {
//		deleteExistingAlbumAndImage(1);
//		createAnAlbumAndImage();
//	}

//	@Transactional
//	private void deleteExistingAlbumAndImage(Integer imageId) {
//		Image image = imageRepository.getImageById(imageId);
//		if (image == null) {
//			return;
//		}
//		Integer albumId = image.getAlbum().getId();
//		imageRepository.deleteImage(imageId);
//		albumRepository.deleteAlbum(albumId);
//	}

	@Before
	@Transactional
	public void createAnAlbumAndImage() {
		Album album = albumRepository.createAlbum(supplyAlbumName());
		assertThat(album.getId(), equalTo(1));
		logger.debug("album.id = {}", album.getId());
		Image image = supplyImage(album);
		imageRepository.persistImage(image);
		assertThat(image.getId(), equalTo(1));
		logger.debug("image.id = {}", image.getId());
	}
}
