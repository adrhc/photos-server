package image.photos.infrastructure.messaging;

import image.infrastructure.messaging.album.AlbumEvent;
import image.infrastructure.messaging.album.AlbumTopic;
import image.infrastructure.messaging.album.registration.AlbumSubscription;
import image.photos.album.services.AlbumExporterService;
import image.photos.config.AppConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.EnumSet;

import static image.infrastructure.messaging.album.AlbumEventTypeEnum.CREATED;
import static image.infrastructure.messaging.album.AlbumEventTypeEnum.UPDATED;
import static image.persistence.entity.enums.AppConfigEnum.album_autoexport;

@Component
@Slf4j
public class AlbumExporterSubscription implements AlbumSubscription {
	private final AlbumTopic albumTopic;
	private final AlbumExporterService exporterService;
	private final AppConfigService appConfigService;
	private Disposable disposable;

	public AlbumExporterSubscription(AlbumTopic albumTopic, AlbumExporterService exporterService, AppConfigService appConfigService) {
		this.albumTopic = albumTopic;
		this.exporterService = exporterService;
		this.appConfigService = appConfigService;
	}

	@Override
	public Disposable subscribe(Flux<AlbumEvent> flux) {
		this.disposable = flux
				.filter(e -> EnumSet.of(CREATED, UPDATED).contains(e.getType()))
				.publishOn(Schedulers.newBoundedElastic(1, Integer.MAX_VALUE, "album-exporter"))
				.subscribe(
						e -> {
							assert !Thread.currentThread().getName().equals("main");
							if (!this.appConfigService.getConfigBool(album_autoexport)) {
								log.info("album auto-export on change is disabled");
								return;
							}
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
