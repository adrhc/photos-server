package image.photos.events.image;

import image.persistence.entity.Album;
import image.persistence.entity.Image;
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
@ToString(exclude = {"album", "image"})
public class ImageEvent implements Serializable {
	private String requestId;
	private EImageEventType type;
	private String imageName;
	private Image image;
	private Album album;
}
