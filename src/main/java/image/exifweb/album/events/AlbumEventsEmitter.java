package image.exifweb.album.events;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by adr on 1/28/18.
 */
@Component
public class AlbumEventsEmitter {
	private static final Logger logger = LoggerFactory.getLogger(AlbumEventsEmitter.class);
	private ThreadLocal<String> requestId = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
	private Subject<AlbumEvent> albumEvents = PublishSubject.<AlbumEvent>create().toSerialized();
//	private PublishSubject<AlbumEvent> albumEvents = PublishSubject.create();


	public void emit(AlbumEvent albumEvent) {
		albumEvent.setRequestId(requestId.get());
		albumEvents.onNext(albumEvent);
	}

	public Observable<AlbumEvent> albumEventsByTypes(
			boolean filterByRequestId,
			EAlbumEventType albumEventType) {
		return albumEvents
				.doOnNext(ae -> {
					logger.debug("album event received:\n\t{}", ae.getAlbum().toString());
					logger.debug("received: {}", ae.getEventType().name());
					logger.debug("accept: {}, acceptable: {}\n\trequestId = {}",
							albumEventType.name(),
							albumEventType.equals(ae.getEventType()),
							ae.getRequestId());
				})
				.filter(ae -> albumEventType.equals(ae.getEventType()))
				.filter(ae -> !filterByRequestId || ae.getRequestId().equals(requestId.get()));
	}

	@PreDestroy
	public void preDestroy() {
		logger.debug("BEGIN");
		albumEvents.onComplete();
	}
}
