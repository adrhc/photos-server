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

	public Disposable subscribeAsync(EAlbumEventType albumEventType,
	                                 Consumer<AlbumEvent> onNext) {
		return albumEventsByTypes(false, EnumSet.of(albumEventType))
				.observeOn(Schedulers.io())
				.subscribe(onNext::accept,
						t -> {
							logger.error(t.getMessage(), t);
							logger.error("[{}]", albumEventType.name());
						});
	}

	public Disposable subscribeAsync(EAlbumEventType albumEventType,
	                                 Predicate<AlbumEvent> filter,
	                                 Consumer<AlbumEvent> onNext) {
		return albumEventsByTypes(false, EnumSet.of(albumEventType))
				.observeOn(Schedulers.io())
				.filter(filter)
				.subscribe(onNext::accept,
						t -> {
							logger.error(t.getMessage(), t);
							logger.error("[{}]", albumEventType.name());
						});
	}

	public Observable<AlbumEvent> albumEventsByTypes(
			boolean filterByRequestId,
			EnumSet<EAlbumEventType> albumEventTypes) {
		return albumEvents
				.doOnNext(ae -> {
					logger.debug("album event received:\n\t{}", ae.getAlbum().toString());
					logger.debug("received: {}", ae.getEventType().name());
					logger.debug("accept: {}, acceptable: {}\n\trequestId = {}",
							albumEventTypes.stream().map(Enum::name).collect(Collectors.joining(", ")),
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
