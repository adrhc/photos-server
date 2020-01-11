package image.photostests.junit5.album;

import image.infrastructure.messaging.album.AlbumEvent;
import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.util.Jpa2ndLevelCacheUtils;
import image.persistence.entity.Album;
import image.photos.album.services.AlbumExporterService;
import image.photos.album.services.AlbumImporterService;
import image.photostests.junit4.testconfig.PhotosTestConfig;
import image.photostests.junit5.app.AppConfigFromClassPath;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static image.infrastructure.messaging.album.AlbumEventTypeEnum.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@Junit5PhotosInMemoryDbConfig
@ContextConfiguration(classes = {
		PhotosTestConfig.class, AlbumImporterServiceTest.Config.class})
class AlbumImporterServiceTest extends AppConfigFromClassPath {
	private static final String SIMFONIA_LALELELOR = "2013-04-20_Simfonia_lalelelor";
	private static final String CASA_URLUIENI = "2017-07-15 Casa Urluieni";
	private static final String MISSING_ALBUM = "MISSING ALBUM";
	private static final int PHOTOS_PER_PAGE = 5;
	@Autowired
	private AlbumImporterService service;
	@Autowired
	private Jpa2ndLevelCacheUtils cacheUtils;
	@Autowired
	private AlbumRepository albumRepository;

	@BeforeEach
	void setup() {
		this.cacheUtils.evictAll();
		this.albumRepository.deleteAll();
		this.defaultAlbumsRoot();
		this.photosPerPage(PHOTOS_PER_PAGE);
	}

	@ParameterizedTest
	@ValueSource(strings = {SIMFONIA_LALELELOR, CASA_URLUIENI})
	void importByAlbumName(String albumToReimport) {
		var albumEvent = this.service.importByAlbumName(albumToReimport);

		assertTrue(albumEvent::isPresent);

		assertThat(albumEvent.get(), allOf(
				hasProperty("type", is(CREATED)),
				hasProperty("entity",
						hasProperty("name", is(albumToReimport)))));

		this.verifyAlbum(albumToReimport);
	}

	@Test
	void importNewAlbumsOnly() throws IOException {
		var albumEvent = this.service.importNewAlbums();

		List<AlbumEvent> events = albumEvent.stream()
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

		assertThat(events, hasItem(
				allOf(
						hasProperty("type", is(CREATED)),
						hasProperty("entity",
								hasProperty("name",
										is(in(List.of(SIMFONIA_LALELELOR, CASA_URLUIENI)))))
				)));

		List.of(SIMFONIA_LALELELOR, CASA_URLUIENI).forEach(this::verifyAlbum);
	}

	@Test
	void importAll() throws IOException {
		// add an album to DB
		this.importByAlbumName(CASA_URLUIENI);

		var albumEvent = this.service.importAll();

		List<AlbumEvent> events = albumEvent.stream()
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

		assertThat(events, hasItem(
				allOf(
						hasProperty("type", is(CREATED)),
						hasProperty("entity",
								hasProperty("name", is(SIMFONIA_LALELELOR)))
				)));

		assertThat(events, hasItem(
				allOf(
						hasProperty("type", is(UPDATED)),
						hasProperty("entity",
								hasProperty("name", is(CASA_URLUIENI)))
				)));

		List.of(SIMFONIA_LALELELOR, CASA_URLUIENI).forEach(this::verifyAlbum);
	}

	@Test
	void reImportMissingPath() {
		var albumEvent = this.service.importByAlbumName(MISSING_ALBUM);

		assertTrue(albumEvent::isPresent);

		assertThat(albumEvent.get(), allOf(
				hasProperty("type", is(MISSING_PATH)),
				hasProperty("entity",
						hasProperty("name", is(MISSING_ALBUM)))));
	}

	private void verifyAlbum(String name) {
		Album album = this.albumRepository.findByName(name);
		assertNotNull(album);
	}

	static class Config {
		@Bean
		AlbumExporterService exporterService() {
			return mock(AlbumExporterService.class);
		}
	}
}
