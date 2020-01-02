package image.photos.infrastructure.events.image;

import image.persistence.entity.Image;
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
public class ImageEvent implements Message<String, ImageEventTypeEnum> {
	private String id;
	private ImageEventTypeEnum type;
	private Image image;

	public static ImageEvent of(Image image, ImageEventTypeEnum eventType) {
		return ImageEvent.builder().image(image).type(eventType).build();
	}
}
