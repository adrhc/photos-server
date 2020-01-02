package image.photos.infrastructure.events.album;

import image.persistence.entity.Album;
import image.photos.infrastructure.events.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by adr on 1/28/18.
 */
@Getter
@Setter
@Builder
@ToString
public class AlbumEvent implements Message<String, AlbumEventTypeEnum> {
	private String id;
	private AlbumEventTypeEnum type;
	private Album album;

	public static AlbumEvent of(Album album, AlbumEventTypeEnum eventType) {
		return AlbumEvent.builder().album(album).type(eventType).build();
	}
}
