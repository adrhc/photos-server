package image.infrastructure.messaging.image.registration;

import image.infrastructure.messaging.core.consumer.Subscription;
import image.infrastructure.messaging.image.ImageEvent;
import image.infrastructure.messaging.image.ImageEventTypeEnum;

public interface ImageSubscription extends Subscription<String, ImageEventTypeEnum, ImageEvent> {
}
