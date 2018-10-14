package image.persistence.repository.junit4.staging.album;

import image.persistence.entity.Album;
import image.persistence.entity.IAlbumSupplier;
import image.persistence.entity.IImageSupplier;
import image.persistence.entity.Image;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.ImageRepository;
import image.persistence.repository.junit4.production.AlbumRepositoryTest;
import image.persistence.repository.junit4.springrunner.SpringRunnerRulesBased;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;

/**
 * Must be a public class otherwise occurs:
 * IllegalAccessException: Class org.junit.runners.model.FrameworkMethod$1 can not access a member of class AlbumRepoWriteTestBase with modifiers "public"
 * <p>
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
	protected Integer albumId;
	protected Image image;
	protected Integer imageId;
	@Autowired
	private PlatformTransactionManager transactionManager;

	@Before
	public void createAnAlbumWithImage() {
		TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
		transactionTemplate.execute((ts) -> {
			this.album = this.albumRepository.createByName(supplyAlbumName());
			this.image = supplyImage(this.album);
			this.imageRepository.persist(this.image);
			return null;
		});
		this.albumId = this.album.getId();
		logger.debug("album.id = {}, album.name = {}", this.album.getId(), this.album.getName());
		this.imageId = this.image.getId();
		logger.debug("image.id = {}, image.name = {}", this.image.getId(), this.image.getName());
	}

	@After
	public void removeAlbumAndImage() {
		this.albumRepository.deleteById(this.albumId);
	}
}
