package image.infrastructure.messaging.album.registration;

import image.infrastructure.messaging.album.AlbumEvent;
import image.infrastructure.messaging.album.AlbumEventTypeEnum;
import image.infrastructure.messaging.core.consumer.Subscription;

public interface AlbumSubscription extends Subscription<String, AlbumEventTypeEnum, AlbumEvent> {
}
