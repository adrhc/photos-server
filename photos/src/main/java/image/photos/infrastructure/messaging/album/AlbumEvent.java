package image.photos.infrastructure.messaging.album;

import image.messaging.message.BaseMessage;
import image.persistence.entity.Album;
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
