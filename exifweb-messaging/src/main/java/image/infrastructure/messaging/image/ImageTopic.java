package image.infrastructure.messaging.image;

import image.infrastructure.messaging.core.destination.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by adr on 1/28/18.
 */
@Component
@Slf4j
public class ImageTopic extends Topic<ImageEventTypeEnum, ImageEvent> {}
