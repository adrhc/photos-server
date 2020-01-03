package image.messaging.destination;

import image.messaging.message.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import javax.annotation.PreDestroy;
import java.util.EnumSet;
import java.util.UUID;

@Slf4j
public abstract class Topic<C extends Enum<C>, E extends Message<String, C>> {
	@Getter
	private final ThreadLocal<String> id =
			ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
	private final DirectProcessor<E> topic = DirectProcessor.create();
	private final FluxSink<E> sink = this.topic.sink();

	public void emit(E albumEvent) {
		if (albumEvent.getId() == null) {
			albumEvent.setId(this.id.get());
		}
		this.sink.next(albumEvent);
	}

	public Flux<E> eventsByType(EnumSet<C> imageEventTypes) {
		return eventsByType(true, imageEventTypes);
	}

	public Flux<E> eventsByType(boolean filterById, EnumSet<C> eventTypes) {
		return this.topic
				.filter(ae -> eventTypes.contains(ae.getType()))
				.filter(ae -> !filterById ||
						ae.getId().equals(this.id.get()));
	}

	@PreDestroy
	public void preDestroy() {
		log.debug("sink.complete");
		this.sink.complete();
	}
}
