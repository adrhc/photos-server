package image.jpa2xtests.repositories;

import image.jpa2x.repositories.album.AlbumRepository;
import image.jpa2x.repositories.appconfig.AppConfigRepository;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.entitytests.IAppConfigSupplier;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static exifweb.util.SuppressExceptionUtils.ignoreExc;

public abstract class ImageTestBase implements IAppConfigSupplier {
	protected Album album;
	@Autowired
	protected AlbumRepository albumRepository;
	@Autowired
	protected AppConfigRepository appConfigRepository;

	/**
	 * Notice that ImageMetadata is generated too and will be used in tests!
	 */
	@BeforeAll
	void setUp(
			@Random(excludes = {"id", "lastUpdate", "cover", "images"})
					Album album,
			@Random(type = Image.class, excludes = {"id", "lastUpdate", "album"})
					List<Image> images
	) {
		// % -> difficult character for LIKE sql operator
		images.forEach(i -> i.setName(i.getName().replace('%', '-')));
		this.album = album;
		this.album.addImages(images);
		this.albumRepository.save(this.album);
		this.appConfigRepository.persist(this.entityAppConfigOf(AppConfigEnum.albums_path, "/dummy-path"));
	}

	/**
	 * this.updateThumbLastModifiedForImg() + this.albumRepository.delete(this.album) yield this:
	 * <p>
	 * ObjectOptimisticLockingFailureException: Object of class [image.persistence.entity.Image] with identifier [1]: optimistic locking failed; nested exception is org.hibernate.StaleObjectStateException: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect) : [image.persistence.entity.Image#1]
	 */
	@AfterAll
	void tearDown() {
		ignoreExc(
				() -> this.albumRepository.deleteById(this.album.getId()),
				() -> this.appConfigRepository.deleteByEnumeratedName(AppConfigEnum.albums_path)
		);
	}
}
