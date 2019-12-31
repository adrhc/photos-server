package image.photos.events.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
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
public class ImageEventsEmitter {
	private static final Logger logger = LoggerFactory.getLogger(ImageEventsEmitter.class);
	private ThreadLocal<String> requestId = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
	private FluxSink<ImageEvent> sink;
	private Flux<ImageEvent> imageEvents = Flux.create(sink -> this.sink = sink);

	public void emit(ImageEvent imageEvent) {
		if (imageEvent.getRequestId() == null) {
			imageEvent.setRequestId(this.requestId.get());
		}
		this.sink.next(imageEvent);
	}

	public Flux<ImageEvent> imageEventsByType(EnumSet<EImageEventType> imageEventTypes) {
		return imageEventsByType(true, imageEventTypes);
	}

	/**
	 * @param filterByRequestId: "true" means to take only current thread's events
	 * @param imageEventTypes:   event types to take
	 */
	public Flux<ImageEvent> imageEventsByType(
			boolean filterByRequestId, EnumSet<EImageEventType> imageEventTypes) {
		return this.imageEvents
				.doOnNext(ie -> {
					// logging events
					logger.debug("image event received:\n\tid = {}, name: {}",
							ie.getImage().getId(), ie.getImage().getName());
					logger.debug("received: {}", ie.getType().name());
					logger.debug("accept: {}, acceptable: {}\n\trequestId = {}",
							imageEventTypes.stream().map(Enum::name)
									.collect(Collectors.joining(", ")),
							imageEventTypes.contains(ie.getType()),
							ie.getRequestId());
				})
				.filter(ae -> imageEventTypes.contains(ae.getType()))
				.filter(ae -> !filterByRequestId || ae.getRequestId().equals(this.requestId.get()));
	}

	@PreDestroy
	public void preDestroy() {
		this.sink.complete();
	}
}
