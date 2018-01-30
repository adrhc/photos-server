package image.exifweb.image.events;

import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;

/**
 * Created by adr on 1/28/18.
 */
public class ImageEventBuilder {
	private ImageEvent imageEvent = new ImageEvent();

	public static ImageEventBuilder of(EImageEventType imageEventType) {
		return new ImageEventBuilder().type(imageEventType);
	}

	public ImageEventBuilder image(Image image) {
		imageEvent.setImage(image);
		return this;
	}

	public ImageEventBuilder imageName(String imageName) {
		imageEvent.setImageName(imageName);
		return this;
	}

	public ImageEventBuilder album(Album album) {
		imageEvent.setAlbum(album);
		return this;
	}

	public ImageEventBuilder type(EImageEventType imageEventType) {
		imageEvent.setEventType(imageEventType);
		return this;
	}

	public ImageEvent build() {
		return imageEvent;
	}
}
