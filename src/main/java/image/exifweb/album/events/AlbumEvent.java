package image.exifweb.album.events;

import image.exifweb.system.persistence.entities.Album;

import java.util.HashMap;

/**
 * Created by adr on 1/28/18.
 */
public class AlbumEvent extends HashMap<EAlbumEventDetail, Object> {
	private EAlbumEventType eventType;

	public AlbumEvent() {
	}

	public AlbumEvent(EAlbumEventType eventType) {
		this.eventType = eventType;
	}

	public Album getAlbum() {
		return getValue(EAlbumEventDetail.ALBUM);
	}

	public void setAlbum(Album album) {
		put(EAlbumEventDetail.ALBUM, album);
	}

	public String getRequestId() {
		return getValue(EAlbumEventDetail.REQUEST_ID);
	}

	public void setRequestId(String requestId) {
		put(EAlbumEventDetail.REQUEST_ID, requestId);
	}

	public EAlbumEventType getEventType() {
		return eventType;
	}

	public void setEventType(EAlbumEventType eventType) {
		this.eventType = eventType;
	}

	public <T> T getValue(EAlbumEventDetail key) {
		return (T) get(key);
	}

	@Override
	public String toString() {
		return super.toString() + "\nAlbumEvent{" +
				"eventType=" + eventType +
				'}';
	}
}
