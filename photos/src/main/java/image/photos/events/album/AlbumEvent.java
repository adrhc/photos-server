package image.photos.events.album;

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
@ToString(exclude = "album")
public class AlbumEvent implements Serializable {
	private String requestId;
	private EAlbumEventType type;
	private Album album;
}
