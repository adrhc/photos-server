package image.infrastructure.messaging.image;

import image.infrastructure.messaging.core.message.BaseMessage;
import image.persistence.entity.Image;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by adr on 1/28/18.
 */
@SuperBuilder
@ToString
public class ImageEvent extends BaseMessage<Image, String, ImageEventTypeEnum> {
	public static ImageEvent of(Image image, ImageEventTypeEnum eventType) {
		return ImageEvent.builder().entity(image).type(eventType).build();
	}

	public static List<ImageEvent> of(Collection<Image> images, ImageEventTypeEnum eventType) {
		return images.stream().map(i -> of(i, eventType)).collect(Collectors.toList());
	}
}
