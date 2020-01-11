package image.photostests.junit5.album;

import image.infrastructure.messaging.album.AlbumEvent;
import image.infrastructure.messaging.album.AlbumEventTypeEnum;
import image.infrastructure.messaging.album.AlbumTopic;
import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.ImageQueryRepository;
import image.persistence.entity.Album;
import image.photos.album.helpers.AlbumHelper;
import image.photos.album.helpers.AlbumPathChecks;
import image.photos.album.services.AlbumImporterService;
import image.photos.image.helpers.ImageHelper;
import image.photos.image.services.ImageImporterService;
import image.photos.infrastructure.database.ImageCUDService;
import image.photos.infrastructure.filestore.FileStoreService;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static image.infrastructure.messaging.album.AlbumEventTypeEnum.CREATED;
import static image.infrastructure.messaging.album.AlbumEventTypeEnum.FAILED_UPDATE;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class AlbumImporterServiceSpy {
	public static final String SIMFONIA_LALELELOR = "2013-04-20_Simfonia_lalelelor";
	public static final String CASA_URLUIENI = "2017-07-15 Casa Urluieni";
	public static final String MISSING_ALBUM = "MISSING ALBUM";

	private static Optional<AlbumEvent> albumEvent(String name, AlbumEventTypeEnum albumEventType) {
		return Optional.of(AlbumEvent.of(new Album(name), albumEventType));
	}

	@Bean
	AlbumImporterService albumImporterService(ImageHelper imageHelper, ImageImporterService imageImporterService, ImageCUDService imageCUDService, ImageQueryRepository imageQueryRepository, AlbumRepository albumRepository, AlbumTopic albumTopic, AlbumPathChecks albumPathChecks, AlbumHelper albumHelper, FileStoreService fileStoreService) throws IOException {
		var bean = spy(new AlbumImporterService(imageHelper, imageImporterService,
				imageCUDService, imageQueryRepository, albumRepository, albumTopic,
				albumPathChecks, albumHelper, fileStoreService));

		var albumEvents = List.of(albumEvent(CASA_URLUIENI, CREATED), albumEvent(SIMFONIA_LALELELOR, CREATED));
		var withFailed = new ArrayList<>(albumEvents);
		Collections.copy(withFailed, albumEvents);
		withFailed.add(albumEvent(MISSING_ALBUM, FAILED_UPDATE));

		doReturn(withFailed).when(bean).importAll();
		doReturn(albumEvent(CASA_URLUIENI, CREATED)).when(bean).importByAlbumName(anyString());
		doReturn(albumEvents).when(bean).importNewAlbums();
		return bean;
	}
}
