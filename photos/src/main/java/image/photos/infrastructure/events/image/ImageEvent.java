package image.photos.infrastructure.events.image;

import image.persistence.entity.Image;
import image.photos.infrastructure.events.BaseMessage;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Created by adr on 1/28/18.
 */
@SuperBuilder
@ToString
public class ImageEvent extends BaseMessage<Image, String, ImageEventTypeEnum> {
	public static ImageEvent of(Image image, ImageEventTypeEnum eventType) {
		return ImageEvent.builder().entity(image).type(eventType).build();
	}
}
