package image.persistence.repository.staging.album;

import image.persistence.entity.Album;
import image.persistence.entity.IAlbumSupplier;
import image.persistence.entity.IImageSupplier;
import image.persistence.entity.Image;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.ImageRepository;
import image.persistence.repository.production.AlbumRepositoryTest;
import image.persistence.repository.springtestconfig.springrunner.SpringRunnerRulesBased;
import image.persistence.repository.util.ITransactionalAction;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Must be a public class otherwise occurs:
 * IllegalAccessException: Class org.junit.runners.model.FrameworkMethod$1 can not access a member of class image.persistence.repository.staging.album.AlbumRepoWriteTestBase with modifiers "public"
 * <p>
 * Created by adr on 2/25/18.
 */
public abstract class AlbumRepoWriteTestBase extends SpringRunnerRulesBased
		implements IImageSupplier, IAlbumSupplier, ITransactionalAction {
	protected static final Logger logger = LoggerFactory.getLogger(AlbumRepositoryTest.class);

	@Inject
	protected AlbumRepository albumRepository;
	@Inject
	protected ImageRepository imageRepository;

	protected Album album;
	protected Integer albumId;
	protected Image image;
	protected Integer imageId;

	@Before
	public void createAnAlbumWithImage() {
		doTransaction(() -> {
			this.album = this.albumRepository.createAlbum(supplyAlbumName());
			this.albumId = this.album.getId();
			logger.debug("album.id = {}, album.name = {}", this.album.getId(), this.album.getName());
			this.image = supplyImage(this.album);
			this.imageRepository.persistImage(this.image);
			this.imageId = this.image.getId();
			logger.debug("image.id = {}, image.name = {}", this.image.getId(), this.image.getName());
		});
	}

	@After
	public void removeAlbumAndImage() {
		this.albumRepository.deleteAlbum(this.albumId);
		logger.debug("removing album.id = {}", this.albumId);
	}
}
