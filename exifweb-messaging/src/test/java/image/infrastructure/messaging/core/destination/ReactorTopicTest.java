package image.infrastructure.messaging.core.destination;

import image.infrastructure.messaging.album.AlbumEvent;
import image.infrastructure.messaging.album.AlbumEventTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ReactorTopicTest {
	@Test
	void preDestroy() {
		ReactorTopic<String, AlbumEventTypeEnum, AlbumEvent> topic = new ReactorTopic<>() {};
		topic.preDestroy();
	}
}
