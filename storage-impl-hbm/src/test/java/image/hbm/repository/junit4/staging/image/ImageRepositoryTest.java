package image.hbm.repository.junit4.staging.image;

import image.cdm.image.ImageRating;
import image.cdm.image.status.EImageStatus;
import image.cdm.image.status.ImageStatus;
import image.hbm.repository.junit4.staging.album.AlbumRepoWriteTestBase;
import image.hbm.repository.springconfig.HbmStagingJdbcDbConfig;
import image.hbm.util.IDateNoMillisSupplier;
import image.persistence.entity.Image;
import image.persistence.entity.image.IImageFlagsUtils;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Date;

/**
 * Created by adr on 2/25/18.
 *
 * @NotThreadSafe: because it has multiple @Test and also @Before (in AlbumRepoWriteTestBase)
 * <p>
 * see also jsr-305:
 * https://aalmiray.github.io/jsr-305/apidocs/javax/annotation/concurrent/NotThreadSafe.html
 */
@NotThreadSafe
@HbmStagingJdbcDbConfig
@Category(HbmStagingJdbcDbConfig.class)
public class ImageRepositoryTest extends AlbumRepoWriteTestBase
		implements IDateNoMillisSupplier, IImageFlagsUtils {
	@Test
	public void updateThumbLastModifiedForImg() {
		Date date = dateNoMilliseconds();
		this.imageRepository.updateThumbLastModifiedForImg(date, this.imageId);
		Image alteredImage = this.imageRepository.getById(this.imageId);
		// @Temporal(TemporalType.TIMESTAMP) thumbLastModified
		// is in fact a java.sql.TimeStamp
		Assert.assertEquals(alteredImage.getImageMetadata()
				.getThumbLastModified().getTime(), date.getTime());
	}

	@Test
	public void changeRating() {
		byte newRating = ImageRating.MIN_RATING;
		if (this.image.getRating() == newRating) {
			newRating++;
		}
		boolean changed = this.imageRepository.changeRating(
				new ImageRating(this.imageId, newRating));
		Assert.assertTrue(changed);
		Image alteredImage = this.imageRepository.getById(this.imageId);
		Assert.assertEquals(alteredImage.getRating(), newRating);
	}

	@Test
	public void changeStatus() {
		EImageStatus newStatus = EImageStatus.DEFAULT;
		if (areEquals(this.image.getFlags(), newStatus)) {
			newStatus = EImageStatus.PRINTABLE;
		}
		boolean changed = this.imageRepository.changeStatus(new ImageStatus(
				this.imageId, newStatus.getValueAsByte()));
		Assert.assertTrue(changed);
		Image alteredImage = this.imageRepository.getById(this.imageId);
		Assert.assertTrue(areEquals(alteredImage.getFlags(), newStatus));
	}

	@Test
	public void markDeleted() {
		boolean changed = this.imageRepository.markDeleted(this.imageId);
		Assert.assertTrue(changed);
		Image alteredImage = this.imageRepository.getById(this.imageId);
		Assert.assertTrue(alteredImage.isDeleted());
	}

	@Test
	public void changeName() {
		this.imageRepository.changeName(this.image.getName() + "-updated", this.imageId);
		Image alteredImage = this.imageRepository.getById(this.imageId);
		Assert.assertEquals(alteredImage.getName(), this.image.getName() + "-updated");
	}
}
