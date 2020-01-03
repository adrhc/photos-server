package image.photos.infrastructure.messaging;

import image.infrastructure.messaging.album.AlbumEvent;
import image.infrastructure.messaging.album.AlbumTopic;
import image.infrastructure.messaging.album.registration.AlbumSubscription;
import image.photos.album.services.AlbumExporterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class AlbumExporterSubscription implements AlbumSubscription {
	private final AlbumTopic albumTopic;
	private final ExecutorService executorService;
	private final AlbumExporterService exporterService;
	private Disposable disposable;

	public AlbumExporterSubscription(AlbumTopic albumTopic, ExecutorService executorService, AlbumExporterService exporterService) {
		this.albumTopic = albumTopic;
		this.executorService = executorService;
		this.exporterService = exporterService;
	}

	@Override
	public Disposable subscribe(Flux<AlbumEvent> flux) {
		disposable = flux
				.publishOn(Schedulers.fromExecutor(this.executorService))
				.subscribe(
						e -> exporterService.writeJsonForAlbumSafe(e.getEntity()),
						t -> log.error(t.getMessage(), t));
		return disposable;
	}

	@PostConstruct
	public void postConstruct() {
		albumTopic.register(this);
	}

	@PreDestroy
	public void preDestroy() {
		this.disposable.dispose();
	}
}
