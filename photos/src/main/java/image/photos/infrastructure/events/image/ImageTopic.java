package image.photos.infrastructure.events.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import javax.annotation.PreDestroy;
import java.util.EnumSet;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by adr on 1/28/18.
 */
@Component
@Slf4j
public class ImageTopic {
	private final ThreadLocal<String> requestId =
			ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
	private final DirectProcessor<ImageEvent> topic = DirectProcessor.create();
	private final FluxSink<ImageEvent> sink = this.topic.sink();

	public void emit(ImageEvent imageEvent) {
		if (imageEvent.getId() == null) {
			imageEvent.setId(this.requestId.get());
		}
		this.sink.next(imageEvent);
	}

	public Flux<ImageEvent> imageEventsByType(EnumSet<ImageEventTypeEnum> imageEventTypes) {
		return imageEventsByType(true, imageEventTypes);
	}

	/**
	 * @param filterByRequestId: "true" means to take only current thread's events
	 * @param imageEventTypes:   event types to take
	 */
	public Flux<ImageEvent> imageEventsByType(
			boolean filterByRequestId, EnumSet<ImageEventTypeEnum> imageEventTypes) {
		return this.topic
				.doOnNext(ie -> {
					// logging events
					log.debug("image event received:\n\tid = {}, name: {}",
							ie.getImage().getId(), ie.getImage().getName());
					log.debug("received: {}", ie.getType().name());
					log.debug("accept: {}, acceptable: {}\n\trequestId = {}",
							imageEventTypes.stream().map(Enum::name)
									.collect(Collectors.joining(", ")),
							imageEventTypes.contains(ie.getType()),
							ie.getId());
				})
				.filter(ae -> imageEventTypes.contains(ae.getType()))
				.filter(ae -> !filterByRequestId || ae.getId().equals(this.requestId.get()));
	}

	@PreDestroy
	public void preDestroy() {
		log.debug("sink.complete");
		this.sink.complete();
	}
}
