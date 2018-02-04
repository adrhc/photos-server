package image.exifweb.album.events;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

	public Disposable subscribe(EAlbumEventType albumEventType,
	                            Consumer<AlbumEvent> onNext, Consumer<? super Throwable> onError) {
		return albumEventsByTypes(false, EnumSet.of(albumEventType))
				.subscribe(onNext::accept, onError::accept);
	}

	public Observable<AlbumEvent> albumEventsByTypes(
			boolean filterByRequestId,
			EnumSet<EAlbumEventType> albumEventTypes) {
		return albumEvents
				.doOnNext(ae -> {
					logger.debug("new album event received:\n{}", ae.getAlbum().toString());
					logger.debug("accept: {}",
							albumEventTypes.stream().map(Enum::name).collect(Collectors.joining(", ")));
					logger.debug("received: {}, filter by type result = {}\nrequestId = {}",
							ae.getEventType().name(),
							albumEventTypes.contains(ae.getEventType()),
							ae.getRequestId());
				})
				.filter(ae -> albumEventTypes.contains(ae.getEventType()))
				.filter(ae -> !filterByRequestId || ae.getRequestId().equals(requestId.get()));
	}

	@PreDestroy
	public void preDestroy() {
		logger.debug("BEGIN");
		albumEvents.onComplete();
	}
}
