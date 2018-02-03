package image.exifweb.album.events;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.UUID;

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

	public Disposable subscribe(boolean filterByRequestId,
	                            EAlbumEventType albumEventType,
	                            Consumer<AlbumEvent> consumer) {
		return albumEvents
				.doOnNext(ae -> {
					logger.debug(ae.getAlbum().toString());
					logger.debug("type accepted = {}, filterByRequestId = {}",
							albumEventType.name(), filterByRequestId);
					logger.debug("ae.eventType = {}, filter by type result = {}\nrequestId = {}",
							ae.getEventType().name(),
							ae.getEventType().equals(albumEventType),
							ae.getRequestId());
				})
				.filter(ae -> ae.getEventType().equals(albumEventType))
				.filter(ae -> !filterByRequestId || ae.getRequestId().equals(requestId.get()))
				.subscribe(consumer);
	}

	public Disposable subscribe(EAlbumEventType albumEventType,
	                            Consumer<AlbumEvent> consumer) {
		return subscribe(false, albumEventType, consumer);
	}

	public String requestId() {
		return requestId.get();
	}

	@PreDestroy
	public void preDestroy() {
		logger.debug("BEGIN");
		albumEvents.onComplete();
	}
}
