package image.photostests.junit5.album;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.infrastructure.messaging.album.AlbumEvent;
import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.ImageQueryRepository;
import image.jpa2x.util.Jpa2ndLevelCacheUtils;
import image.persistence.entity.Album;
import image.photos.album.services.AlbumExporterService;
import image.photos.album.services.AlbumImporterService;
import image.photos.infrastructure.filestore.FileStoreService;
import image.photos.infrastructure.filestore.FileStoreServiceImpl;
import image.photostests.junit4.testconfig.PhotosTestConfig;
import image.photostests.junit5.app.AppConfigFromClassPath;
import image.photostests.junit5.testconfig.Junit5PhotosInMemoryDbConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static image.infrastructure.messaging.album.AlbumEventTypeEnum.*;
import static image.jpa2x.util.PathUtils.fileName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@NotThreadSafe
@Junit5PhotosInMemoryDbConfig
@ContextConfiguration(classes = {
		PhotosTestConfig.class, AlbumImporterServiceTest.Config.class})
@TestPropertySource(properties = "hibernate.show_sql=false")
class AlbumImporterServiceTest extends AppConfigFromClassPath {
	private static final String SIMFONIA_LALELELOR = "2013-04-20_Simfonia_lalelelor";
	private static final String CASA_URLUIENI = "2017-07-15 Casa Urluieni";
	private static final String MISSING_ALBUM = "MISSING ALBUM";
	private static final String IMAGE = "DSC_0383.jpg";
	private static final Map<String, Integer> COUNT = Map.of(SIMFONIA_LALELELOR, 11, CASA_URLUIENI, 45);
	private static final int PHOTOS_PER_PAGE = 5;
	@Autowired
	private AlbumImporterService service;
	@Autowired
	private Jpa2ndLevelCacheUtils cacheUtils;
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private ImageQueryRepository imageQueryRepository;

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

		// check that the album is reported as created
		assertThat(albumEvent.get(), allOf(
				hasProperty("type", is(CREATED)),
				hasProperty("entity",
						hasProperty("name", is(albumToReimport)))));

		// check that all SIMFONIA_LALELELOR available images were imported
		assertEquals(COUNT.get(albumToReimport), this.imageQueryRepository.countByAlbum_name(albumToReimport));

		// verify that the album exists in database
		this.verifyAlbum(albumToReimport);
	}

	@Test
	void importNewAlbumsOnly() throws IOException {
		var albumEvent = this.service.importNewAlbums();

		List<AlbumEvent> events = albumEvent.stream()
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

		// check that SIMFONIA_LALELELOR and CASA_URLUIENI are reported as created
		assertThat(events, hasItem(
				allOf(
						hasProperty("type", is(CREATED)),
						hasProperty("entity",
								hasProperty("name",
										is(in(List.of(SIMFONIA_LALELELOR, CASA_URLUIENI)))))
				)));

		// check that all SIMFONIA_LALELELOR available images were imported
		assertEquals(COUNT.get(SIMFONIA_LALELELOR), this.imageQueryRepository.countByAlbum_name(SIMFONIA_LALELELOR));

		// check that all SIMFONIA_LALELELOR available images were imported
		assertEquals(COUNT.get(CASA_URLUIENI), this.imageQueryRepository.countByAlbum_name(CASA_URLUIENI));

		// verify that the albums exist in database
		List.of(SIMFONIA_LALELELOR, CASA_URLUIENI).forEach(this::verifyAlbum);
	}

	@Test
	void importAll() throws IOException {
		// have an existing album into DB in order to update it
		// see also Config.fileStoreService below
		this.importByAlbumName(SIMFONIA_LALELELOR);

		// check that all SIMFONIA_LALELELOR available images were imported
		assertEquals(COUNT.get(SIMFONIA_LALELELOR), this.imageQueryRepository.countByAlbum_name(SIMFONIA_LALELELOR));

		var albumEvent = this.service.importAll();

		List<AlbumEvent> events = albumEvent.stream()
				.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

		// check that CASA_URLUIENI is reported as created
		assertThat(events, hasItem(
				allOf(
						hasProperty("type", is(CREATED)),
						hasProperty("entity",
								hasProperty("name", is(CASA_URLUIENI)))
				)));

		// check that SIMFONIA_LALELELOR is reported as updated
		assertThat(events, hasItem(
				allOf(
						hasProperty("type", is(UPDATED)),
						hasProperty("entity",
								hasProperty("name", is(SIMFONIA_LALELELOR)))
				)));

		assertEquals(COUNT.get(SIMFONIA_LALELELOR) - 1,
				this.imageQueryRepository.countByAlbum_name(SIMFONIA_LALELELOR),
				"1 image should be removed from " + SIMFONIA_LALELELOR);
		assertFalse(this.imageQueryRepository.existsByNameAndAlbumName(IMAGE, SIMFONIA_LALELELOR),
				"Image " + IMAGE + " should be deleted!");

		// check that all SIMFONIA_LALELELOR available images were imported
		assertEquals(COUNT.get(CASA_URLUIENI), this
				.imageQueryRepository.countByAlbum_name(CASA_URLUIENI));

		// verify that the albums exist in database
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

		assertFalse(this.albumRepository.existsByName(MISSING_ALBUM), MISSING_ALBUM + " shouldn't exists!");
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

		@Bean
		FileStoreService fileStoreService(ObjectMapper mapper, ImageQueryRepository queryRepository) throws IOException {
			FileStoreService delegate = new FileStoreServiceImpl(mapper);
			FileStoreService storeServiceSpy = spy(delegate);

			doAnswer(invocation -> {
				Path path = invocation.getArgument(0);
				if (fileName(path).equals(IMAGE) &&
						queryRepository.existsByNameAndAlbumName(IMAGE, SIMFONIA_LALELELOR)) {
					// When the related path-album exists in DB we simulate a missing path situation.
					// importAll(): simulate file deleted when calling FileStoreService.lastModifiedTime
					throw new FileNotFoundException(path.toString());
				} else {
					return delegate.lastModifiedTime(path);
				}
			}).when(storeServiceSpy).lastModifiedTime(Mockito.any(Path.class));

			// simulate a missing file
			doAnswer(invocation -> {
				Path path = invocation.getArgument(0);
				return Stream.concat(delegate.walk(path),
						Stream.of(Path.of("missing-file")));
			}).when(storeServiceSpy).walk(Mockito.any(Path.class));

			return storeServiceSpy;
		}
	}
}
