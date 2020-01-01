package image.photos.infrastructure.events.album;

import image.persistence.entity.Album;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by adr on 1/28/18.
 */
@Getter
@Setter
@Builder
@ToString
public class AlbumEvent implements Serializable {
	private String requestId;
	private AlbumEventTypeEnum type;
	private Album album;
}
