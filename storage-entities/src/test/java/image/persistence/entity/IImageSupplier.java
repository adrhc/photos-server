package image.persistence.entity;

import exifweb.util.random.IEnhancedRandom;
import exifweb.util.random.IPositiveIntegerRandom;
import image.cdm.image.status.EImageStatus;
import image.persistence.entity.image.IImageFlagsUtils;
import image.persistence.entity.image.ImageMetadata;

import java.util.Date;

/**
 * Created by adr on 2/25/18.
 */
public interface IImageSupplier extends IPositiveIntegerRandom, IEnhancedRandom, IImageFlagsUtils {
	default Image supplyImage(Album album) {
		Image image = supplyImage();
		image.setAlbum(album);
		return image;
	}

	default Image supplyImage() {
		int random = randomPositiveInt();
		ImageMetadata imageMetadata = new ImageMetadata();
		Date date = new Date();
		imageMetadata.setDateTime(date);
		imageMetadata.getExifData().setDateTimeOriginal(date);
		imageMetadata.setThumbLastModified(date);
		Image image = new Image();
		image.setImageMetadata(imageMetadata);
		image.setName("image-" + random);
		image.setRating((byte) (random % 6));
		image.setFlags(of(EImageStatus.findByValue((int) Math.pow(2, random % 5))));
		return image;
	}
}
