package image.photos.events.image;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
	private Subject<ImageEvent> imageEvents = PublishSubject.<ImageEvent>create().toSerialized();

	public void emit(ImageEvent imageEvent) {
		imageEvent.setRequestId(this.requestId.get());
		this.imageEvents.onNext(imageEvent);
	}

	/**
	 * @param filterByRequestId: "true" means to take only current thread's events
	 * @param imageEventTypes:   event types to take
	 */
	public Observable<ImageEvent> imageEventsByType(
			boolean filterByRequestId, EnumSet<EImageEventType> imageEventTypes) {
		return this.imageEvents
				.doOnNext(ie -> {
					// logging events
					logger.debug("image event received:\n\tid = {}, name: {}",
							ie.getImage().getId(), ie.getImage().getName());
					logger.debug("received: {}", ie.getEventType().name());
					logger.debug("accept: {}, acceptable: {}\n\trequestId = {}",
							imageEventTypes.stream().map(Enum::name)
									.collect(Collectors.joining(", ")),
							imageEventTypes.contains(ie.getEventType()),
							ie.getRequestId());
				})
				.filter(ae -> imageEventTypes.contains(ae.getEventType()))
				.filter(ae -> !filterByRequestId || ae.getRequestId().equals(this.requestId.get()));
	}

	@PreDestroy
	public void preDestroy() {
		this.imageEvents.onComplete();
	}
}
