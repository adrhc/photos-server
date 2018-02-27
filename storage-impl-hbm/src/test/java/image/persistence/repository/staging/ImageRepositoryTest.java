package image.persistence.repository.staging;

import image.cdm.image.ImageRating;
import image.cdm.image.ImageStatus;
import image.persistence.entity.Image;
import image.persistence.entity.enums.EImageStatus;
import image.persistence.repository.springtestconfig.TestJdbcDsTestConfig;
import image.persistence.repository.staging.album.AlbumRepoWriteTestBase;
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
@TestJdbcDsTestConfig
@Category(TestJdbcDsTestConfig.class)
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
		this.imageRepository.changeRating(new ImageRating(this.imageId, (byte) 3));
		Image alteredImage = this.imageRepository.getImageById(this.imageId);
		Assert.assertEquals(alteredImage.getRating(), 3);
	}

	@Test
	public void changeStatus() throws Exception {
		this.imageRepository.changeStatus(new ImageStatus(
				this.imageId, EImageStatus.PRINTABLE.getValue()));
		Image alteredImage = this.imageRepository.getImageById(this.imageId);
		Assert.assertEquals(alteredImage.getStatus(), EImageStatus.PRINTABLE.getValue());
	}

	@Test
	public void persistImage() throws Exception {

	}

	@Test
	public void markDeleted() throws Exception {

	}

	@Test
	public void deleteImage() throws Exception {

	}

	@Test
	public void changeName() throws Exception {

	}

}