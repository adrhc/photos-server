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

	protected Album album;
	protected Image image;

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
		this.album = albumRepository.createAlbum(supplyAlbumName());
		logger.debug("album.id = {}", album.getId());
		this.image = supplyImage(album);
		imageRepository.persistImage(image);
		logger.debug("image.id = {}", image.getId());
	}
}
