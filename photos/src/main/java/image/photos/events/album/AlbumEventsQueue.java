package image.photos.events.album;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import javax.annotation.PreDestroy;
import java.util.UUID;

/**
 * Created by adr on 1/28/18.
 */
@Component
public class AlbumEventsQueue {
	private static final Logger logger = LoggerFactory.getLogger(AlbumEventsQueue.class);
	private ThreadLocal<String> requestId = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
	private FluxSink<AlbumEvent> sink;
	private Flux<AlbumEvent> albumEvents = Flux.create(sink -> this.sink = sink);

	public void emit(AlbumEvent albumEvent) {
		if (albumEvent.getRequestId() == null) {
			albumEvent.setRequestId(this.requestId.get());
		}
		this.sink.next(albumEvent);
	}

	public Flux<AlbumEvent> albumEventsByTypes(
			boolean filterByRequestId,
			EAlbumEventType albumEventType) {
		return this.albumEvents
				.doOnNext(ae -> {
					logger.debug("album event received:\n\t{}", ae.getAlbum().toString());
					logger.debug("received: {}", ae.getType().name());
					logger.debug("accept: {}, acceptable: {}\n\trequestId = {}",
							albumEventType.name(),
							albumEventType.equals(ae.getType()),
							ae.getRequestId());
				})
				.filter(ae -> albumEventType.equals(ae.getType()))
				.filter(ae -> !filterByRequestId || ae.getRequestId().equals(this.requestId.get()));
	}

	@PreDestroy
	public void preDestroy() {
		logger.debug("BEGIN");
		this.sink.complete();
	}
}
