package image.photos.infrastructure.events.album;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import javax.annotation.PreDestroy;
import java.util.EnumSet;
import java.util.UUID;

/**
 * Created by adr on 1/28/18.
 */
@Component
@Slf4j
public class AlbumTopic {
	@Getter
	private final ThreadLocal<String> requestId =
			ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
	private final DirectProcessor<AlbumEvent> topic = DirectProcessor.create();
	private final FluxSink<AlbumEvent> sink = this.topic.sink();

	public void emit(AlbumEvent albumEvent) {
		if (albumEvent.getId() == null) {
			albumEvent.setId(this.requestId.get());
		}
		this.sink.next(albumEvent);
	}

	public Flux<AlbumEvent> albumEventsByTypes(
			boolean filterByRequestId,
			EnumSet<AlbumEventTypeEnum> albumEventTypes) {
		return this.topic
				.filter(ae -> albumEventTypes.contains(ae.getType()))
				.filter(ae -> !filterByRequestId ||
						ae.getId().equals(this.requestId.get()));
	}

	@PreDestroy
	public void preDestroy() {
		log.debug("sink.complete");
		this.sink.complete();
	}
}
