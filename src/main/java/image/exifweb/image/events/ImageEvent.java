package image.exifweb.image.events;

import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;

import java.util.HashMap;

/**
 * Created by adr on 1/28/18.
 */
public class ImageEvent extends HashMap<EImageEventDetail, Object> {
	private EImageEventType eventType;

	public ImageEvent() {
	}

	public ImageEvent(EImageEventType eventType) {
		this.eventType = eventType;
	}

	public Image getImage() {
		return getValue(EImageEventDetail.IMAGE);
	}

	public void setImage(Image album) {
		put(EImageEventDetail.IMAGE, album);
	}

	public Album getAlbum() {
		return getValue(EImageEventDetail.ALBUM);
	}

	public void setAlbum(Album album) {
		put(EImageEventDetail.ALBUM, album);
	}

	public String getRequestId() {
		return getValue(EImageEventDetail.REQUEST_ID);
	}

	public void setRequestId(String requestId) {
		put(EImageEventDetail.REQUEST_ID, requestId);
	}

	public String getImageName() {
		return getValue(EImageEventDetail.IMAGE_NAME);
	}

	public void setImageName(String albumName) {
		put(EImageEventDetail.IMAGE_NAME, albumName);
	}

	public EImageEventType getEventType() {
		return eventType;
	}

	public void setEventType(EImageEventType eventType) {
		this.eventType = eventType;
	}

	public <T> T getValue(EImageEventDetail key) {
		return (T) get(key);
	}
}
