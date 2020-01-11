package image.photostests.junit5.album;

import image.infrastructure.messaging.album.AlbumEvent;
import image.photos.album.services.AlbumImporterService;
import image.photostests.junit4.testconfig.PhotosTestConfig;
import image.photostests.junit5.testconfig.Junit5PhotosStageDbConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static image.infrastructure.messaging.album.AlbumEventTypeEnum.FAILED_UPDATE;
import static image.photostests.junit5.album.AlbumImporterServiceSpy.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Junit5PhotosStageDbConfig
@ContextConfiguration(classes = {PhotosTestConfig.class, AlbumImporterServiceSpy.class})
class AlbumImporterServiceTest {
	@Autowired
	private AlbumImporterService service;

	@Test
	void importAll() throws Exception {
		List<AlbumEvent> albumEvents = this.service.importAll().stream()
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
		List<String> albumNames = this.service.importAll().stream()
				.filter(Optional::isPresent).map(Optional::get)
				.map(it -> it.getEntity().getName())
				.collect(Collectors.toList());
		assertThat(albumNames, containsInAnyOrder(CASA_URLUIENI, SIMFONIA_LALELELOR, MISSING_ALBUM));
		assertThat(albumEvents, hasItem(allOf(
				hasProperty("type", is(FAILED_UPDATE)),
				hasProperty("entity",
						hasProperty("name", is(MISSING_ALBUM))))));
	}
}
