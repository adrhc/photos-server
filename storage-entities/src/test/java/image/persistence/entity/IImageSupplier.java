package image.persistence.entity;

import image.cdm.image.EImageStatus;
import image.persistence.entity.image.ImageMetadata;
import image.persistence.util.IPositiveRandom;

import java.util.Date;

/**
 * Created by adr on 2/25/18.
 */
public interface IImageSupplier extends IPositiveRandom {
	default Image supplyImage(Album album) {
		Image image = supplyImage();
		image.setAlbum(album);
		return image;
	}

	default Image supplyImage() {
		int random = positiveRandom();
		ImageMetadata imageMetadata = new ImageMetadata();
		Date date = new Date();
		imageMetadata.setDateTime(date);
		imageMetadata.getExifData().setDateTimeOriginal(date);
		imageMetadata.setThumbLastModified(date);
		Image image = new Image();
		image.setImageMetadata(imageMetadata);
		image.setName("image-" + random);
		image.setRating((byte) (random % 6));
		image.setStatus(EImageStatus.findByValue((int)
				Math.pow(2, random % 5)).getValueAsByte());
		return image;
	}
}