package image.photos.infrastructure.events.album;

import image.persistence.entity.Album;
import image.photos.infrastructure.events.BaseMessage;
import lombok.experimental.SuperBuilder;

/**
 * Created by adr on 1/28/18.
 */
@SuperBuilder
public class AlbumEvent extends BaseMessage<Album, String, AlbumEventTypeEnum> {
	public static AlbumEvent of(Album album, AlbumEventTypeEnum eventType) {
		return AlbumEvent.builder().entity(album).type(eventType).build();
	}
}
