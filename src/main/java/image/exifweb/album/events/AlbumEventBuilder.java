package image.exifweb.album.events;

import image.exifweb.persistence.Album;

/**
 * Created by adr on 1/28/18.
 */
public class AlbumEventBuilder {
	private AlbumEvent albumEvent = new AlbumEvent();

	public static AlbumEventBuilder of(EAlbumEventType albumEventType) {
		return new AlbumEventBuilder().type(albumEventType);
	}

	public AlbumEventBuilder album(Album album) {
		albumEvent.setAlbum(album);
		return this;
	}

	public AlbumEventBuilder albumName(String albumName) {
		albumEvent.setAlbumName(albumName);
		return this;
	}

	public AlbumEventBuilder type(EAlbumEventType albumEventType) {
		albumEvent.setEventType(albumEventType);
		return this;
	}

	public AlbumEvent build() {
		return albumEvent;
	}
}
