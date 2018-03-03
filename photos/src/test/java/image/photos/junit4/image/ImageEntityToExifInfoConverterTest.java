package image.photos.junit4.image;

import image.cdm.image.ExifInfo;
import image.persistence.entity.IAlbumSupplier;
import image.persistence.entity.IImageSupplier;
import image.persistence.entity.Image;
import image.photos.image.ImageMetadataEntityToDTOConverter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by adr on 2/10/18.
 */
public class ImageEntityToExifInfoConverterTest implements IImageSupplier, IAlbumSupplier {
	private static final Logger logger =
			LoggerFactory.getLogger(ImageEntityToExifInfoConverterTest.class);

	/**
	 * https://www.mkyong.com/hibernate/java-lang-classformaterror-absent-code-attribute-in-method-that-is-not-native-or-abstract-in-class-file/
	 *
	 * @throws IOException
	 */
	@Test
	public void convert() throws IOException {
		Image image = supplyImage();
		// some versions of beanutils fail with Date null
		image.getImageMetadata().setDateTime(null);
		logger.debug(image.toString());
		ImageMetadataEntityToDTOConverter converter = new ImageMetadataEntityToDTOConverter();
		ExifInfo exifInfo = converter.convert(image);
		Assert.assertEquals(exifInfo.getDateTimeOriginal(),
				image.getImageMetadata().getExifData().getDateTimeOriginal());
	}
}
