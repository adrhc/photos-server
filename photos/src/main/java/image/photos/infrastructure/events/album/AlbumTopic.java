package image.photos.infrastructure.events.album;

import image.photos.infrastructure.events.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by adr on 1/28/18.
 */
@Component
@Slf4j
public class AlbumTopic extends Topic<AlbumEventTypeEnum, AlbumEvent> {}
