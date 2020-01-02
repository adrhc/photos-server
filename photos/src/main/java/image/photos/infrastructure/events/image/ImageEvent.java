package image.photos.infrastructure.events.image;

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
@ToString
public class ImageEvent implements Serializable {
	private String requestId;
	private ImageEventTypeEnum type;
	private Image image;

	public static ImageEvent of(Image image, ImageEventTypeEnum eventType) {
		return ImageEvent.builder().image(image).type(eventType).build();
	}
}
