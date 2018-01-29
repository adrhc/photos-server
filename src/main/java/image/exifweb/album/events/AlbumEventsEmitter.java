package image.exifweb.album.events;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * Created by adr on 1/28/18.
 */
@Component
public class AlbumEventsEmitter {
    private PublishSubject<AlbumEvent> albumEvents = PublishSubject.create();

    public void emit(AlbumEvent albumEvent) {
        albumEvents.onNext(albumEvent);
    }

    public Disposable subscribe(EAlbumEventType albumEventType,
                                String requestId, Consumer<AlbumEvent> consumer) {
        return albumEvents
                .filter(ae -> ae.getEventType().equals(albumEventType))
                .filter(ae -> ae.getRequestId().equals(requestId))
                .subscribe(consumer);
    }

    public Disposable subscribe(EAlbumEventType albumEventType,
                                Consumer<AlbumEvent> consumer) {
        return albumEvents
                .filter(ae -> ae.getEventType().equals(albumEventType))
                .subscribe(consumer);
    }

    @PreDestroy
    public void preDestroy() {
        albumEvents.onComplete();
    }
}
