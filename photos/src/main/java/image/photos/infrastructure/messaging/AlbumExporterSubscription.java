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
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;

import static image.infrastructure.messaging.album.AlbumEventTypeEnum.CREATED;
import static image.infrastructure.messaging.album.AlbumEventTypeEnum.UPDATED;

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
		this.disposable = flux
				.filter(e -> EnumSet.of(CREATED, UPDATED).contains(e.getType()))
				.publishOn(Schedulers.fromExecutor(this.executorService))
				.subscribe(
						e -> {
							assert !Thread.currentThread().getName().equals("main");
							this.exporterService.writeJsonForAlbumSafe(e.getEntity());
						},
						t -> log.error(t.getMessage(), t));
		return this.disposable;
	}

	@PostConstruct
	public void postConstruct() {
		this.albumTopic.register(this);
	}

	@PreDestroy
	public void preDestroy() {
		this.disposable.dispose();
	}
}
