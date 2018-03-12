package image.persistence.repository.junit4.staging.image;

import image.cdm.image.ImageRating;
import image.cdm.image.status.EImageStatus;
import image.cdm.image.status.ImageStatus;
import image.persistence.entity.Image;
import image.persistence.repository.junit4.staging.album.AlbumRepoWriteTestBase;
import image.persistence.repository.springconfig.HbmStagingJdbcDbConfig;
import image.persistence.repository.util.IDateNoMillisSupplier;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Date;

/**
 * Created by adr on 2/25/18.
 */
@NotThreadSafe
@HbmStagingJdbcDbConfig
@Category(HbmStagingJdbcDbConfig.class)
public class ImageRepositoryTest extends AlbumRepoWriteTestBase
		implements IDateNoMillisSupplier {
	@Test
	public void updateThumbLastModifiedForImg() throws Exception {
		Date date = dateNoMilliseconds();
		this.imageRepository.updateThumbLastModifiedForImg(date, this.imageId);
		Image alteredImage = this.imageRepository.getImageById(this.imageId);
		// @Temporal(TemporalType.TIMESTAMP) thumbLastModified
		// is in fact a java.sql.TimeStamp
		Assert.assertEquals(alteredImage.getImageMetadata()
				.getThumbLastModified().getTime(), date.getTime());
	}

	@Test
	public void changeRating() throws Exception {
		byte newRating = ImageRating.MIN_RATING;
		if (this.image.getRating() == newRating) {
			newRating++;
		}
		boolean changed = this.imageRepository.changeRating(
				new ImageRating(this.imageId, newRating));
		Assert.assertTrue(changed);
		Image alteredImage = this.imageRepository.getImageById(this.imageId);
		Assert.assertEquals(alteredImage.getRating(), newRating);
	}

	@Test
	public void changeStatus() throws Exception {
		EImageStatus newStatus = EImageStatus.DEFAULT;
		if (this.image.getStatus() == newStatus.getValueAsByte()) {
			newStatus = EImageStatus.PRINTABLE;
		}
		boolean changed = this.imageRepository.changeStatus(new ImageStatus(
				this.imageId, newStatus.getValueAsByte()));
		Assert.assertTrue(changed);
		Image alteredImage = this.imageRepository.getImageById(this.imageId);
		Assert.assertEquals(alteredImage.getStatus(), newStatus.getValueAsByte());
	}

	@Test
	public void markDeleted() throws Exception {
		boolean changed = this.imageRepository.markDeleted(this.imageId);
		Assert.assertTrue(changed);
		Image alteredImage = this.imageRepository.getImageById(this.imageId);
		Assert.assertTrue(alteredImage.isDeleted());
	}

	@Test
	public void changeName() throws Exception {
		this.imageRepository.changeName(this.image.getName() + "-updated", this.imageId);
		Image alteredImage = this.imageRepository.getImageById(this.imageId);
		Assert.assertEquals(alteredImage.getName(), this.image.getName() + "-updated");
	}
}