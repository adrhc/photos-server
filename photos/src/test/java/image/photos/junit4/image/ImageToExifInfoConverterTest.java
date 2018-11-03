package image.photos.junit4.image;

import image.cdm.image.ExifInfo;
import image.persistence.entity.Image;
import image.persistence.entitytests.IImageSupplier;
import image.photos.image.ImageToExifInfoConverter;
import image.photos.junit4.misc.MiscTestCategory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

/**
 * Created by adr on 2/10/18.
 */
@Category(MiscTestCategory.class)
public class ImageToExifInfoConverterTest implements IImageSupplier {
	private Image image;

	@Before
	public void setUp() {
		this.image = supplyImage();
		// some versions of beanutils fail with Date null
		this.image.getImageMetadata().setDateTime(null);
	}

	/**
	 * https://www.mkyong.com/hibernate/java-lang-classformaterror-absent-code-attribute-in-method-that-is-not-native-or-abstract-in-class-file/
	 *
	 * @throws IOException
	 */
	@Test
	public void convert() {
		ImageToExifInfoConverter converter = new ImageToExifInfoConverter();
		ExifInfo exifInfo = converter.convert(this.image);
		Assert.assertEquals("name", this.image.getName(), exifInfo.getName());
		Assert.assertEquals("dateTime",
				this.image.getImageMetadata().getDateTime(),
				exifInfo.getDateTime());
		Assert.assertEquals("dateTimeOriginal",
				this.image.getImageMetadata().getExifData().getDateTimeOriginal(),
				exifInfo.getDateTimeOriginal());
	}
}
