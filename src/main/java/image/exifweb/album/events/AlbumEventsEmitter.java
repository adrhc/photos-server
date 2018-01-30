package image.exifweb.album.events;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.UUID;

/**
 * Created by adr on 1/28/18.
 */
@Component
public class AlbumEventsEmitter {
	private ThreadLocal<String> requestId = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
	private PublishSubject<AlbumEvent> albumEvents = PublishSubject.create();

	public void emit(AlbumEvent albumEvent) {
		albumEvent.setRequestId(requestId.get());
		albumEvents.onNext(albumEvent);
	}

	public Disposable subscribe(boolean filterByRequestId,
	                            EAlbumEventType albumEventType,
	                            Consumer<AlbumEvent> consumer) {
		return albumEvents
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
		albumEvents.onComplete();
	}
}
