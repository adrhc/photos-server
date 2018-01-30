package image.exifweb.image.events;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.EnumSet;
import java.util.UUID;

/**
 * Created by adr on 1/28/18.
 */
@Component
public class ImageEventsEmitter {
	private ThreadLocal<String> requestId = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
	private PublishSubject<ImageEvent> imageEvents = PublishSubject.create();

	public void emit(ImageEvent imageEvent) {
		imageEvent.setRequestId(requestId.get());
		imageEvents.onNext(imageEvent);
	}

	public Observable<ImageEvent> imageEventsByType(
			boolean filterByRequestId,
			EnumSet<EImageEventType> imageEventTypes) {
		return imageEvents
				.filter(ae -> imageEventTypes.contains(ae.getEventType()))
				.filter(ae -> !filterByRequestId || ae.getRequestId().equals(requestId.get()));
	}

	public Observable<ImageEvent> imageEventsByType(
			EnumSet<EImageEventType> imageEventTypes) {
		return imageEventsByType(false, imageEventTypes);
	}

	public String requestId() {
		return requestId.get();
	}

	@PreDestroy
	public void preDestroy() {
		imageEvents.onComplete();
	}
}
