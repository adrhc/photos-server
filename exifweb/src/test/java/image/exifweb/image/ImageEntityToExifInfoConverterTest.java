package image.exifweb.image;

import image.cdm.image.ExifInfo;
import image.persistence.entity.Album;
import image.persistence.entity.Image;
import image.persistence.entity.image.ImageMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by adr on 2/10/18.
 */
public class ImageEntityToExifInfoConverterTest {
	private static final Logger logger =
			LoggerFactory.getLogger(ImageEntityToExifInfoConverterTest.class);


	/**
	 * https://www.mkyong.com/hibernate/java-lang-classformaterror-absent-code-attribute-in-method-that-is-not-native-or-abstract-in-class-file/
	 *
	 * @throws IOException
	 */
	@Test
	public void convert() throws IOException {
		Image image = imageSupplier();
		logger.debug(image.toString());
		ImageMetadataEntityToDTOConverter converter = new ImageMetadataEntityToDTOConverter();
		ExifInfo exifInfo = converter.convert(image);
		Assert.assertEquals(exifInfo.getDateTimeOriginal(),
				image.getImageMetadata().getExifData().getDateTimeOriginal());
	}

	private Image imageSupplier() {
		int random = ThreadLocalRandom.current().nextInt();
		Date date = new Date();
		ImageMetadata imageMetadata = new ImageMetadata();
		imageMetadata.setDateTime(null);// some versions of beanutils fail with Date null
		imageMetadata.getExifData().setDateTimeOriginal(date);
		Image image = new Image();
		image.setImageMetadata(imageMetadata);
		image.setDeleted(false);
		image.setName("test image " + random);
		image.setId(random);
		image.setLastUpdate(date);
		image.setRating((byte) (random % 6));
		image.setStatus((byte) Math.pow(2, random % 5));
		Album album = albumSupplier();
		album.addImage(image);
		return image;
	}

	private Album albumSupplier() {
		int random = ThreadLocalRandom.current().nextInt();
		Album album = new Album();
		album.setLastUpdate(new Date());
		album.setId(random);
		album.setDeleted(false);
		album.setName("test album " + random);
		return album;
	}
}
