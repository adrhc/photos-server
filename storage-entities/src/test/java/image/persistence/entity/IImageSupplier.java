package image.persistence.entity;

import image.persistence.entity.image.ImageMetadata;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by adr on 2/25/18.
 */
public interface IImageSupplier {
	default Image supplyImage(Album album) {
		Image image = supplyImage();
		image.setAlbum(album);
		return image;
	}

	default Image supplyImage() {
		int random = ThreadLocalRandom.current().nextInt();
		ImageMetadata imageMetadata = new ImageMetadata();
		Date date = new Date();
		imageMetadata.setDateTime(date);
		imageMetadata.getExifData().setDateTimeOriginal(date);
		imageMetadata.setThumbLastModified(date);
		Image image = new Image();
		image.setImageMetadata(imageMetadata);
		image.setName("image-" + random);
		image.setRating((byte) (random % 6));
		image.setStatus((byte) Math.pow(2, random % 5));
		return image;
	}
}
