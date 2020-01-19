package image.exifwebtests.album.importer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import image.cdm.album.page.AlbumPage;
import image.exifweb.web.json.JsonStringValue;
import image.exifwebtests.config.WebInMemoryDbConfig;
import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.ImageQueryRepository;
import image.jpa2x.util.Jpa2ndLevelCacheUtils;
import image.persistence.entity.Album;
import image.persistence.repository.ESortType;
import image.photos.album.services.AlbumPageService;
import image.photos.infrastructure.filestore.FileStoreService;
import image.photostests.junit5.app.AppConfigFromClassPath;
import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.NotThreadSafe;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneaked;
import static exifweb.util.concurrency.ThreadUtils.safeSleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Is @NotThreadSafe because @BeforeEach deletes all albums!
 */
@NotThreadSafe
@WebInMemoryDbConfig
@Tag("controller")
@Slf4j
class AlbumImporterCtrlIT extends AppConfigFromClassPath {
	private static final String SIMFONIA_LALELELOR = "2013-04-20_Simfonia_lalelelor";
	private static final String CASA_URLUIENI = "2017-07-15 Casa Urluieni";
	private static final String MISSING_ALBUM = "MISSING ALBUM";
	private static final Map<String, Integer> COUNT = Map.of(SIMFONIA_LALELELOR, 11, CASA_URLUIENI, 45);
	private static final int PHOTOS_PER_PAGE = 5;
	/**
	 * 2.20.1. The TempDirectory Extension
	 * <p>
	 * If you wish to retain a single reference to a temp directory across lifecycle methods and the current test method, please use field injection, by annotating a non-private instance field with @TempDir.
	 */
	@TempDir
	static Path tempDir;
	// used for photos_json_FS_path
	static Path jsonDir;
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private FileStoreService fileStoreService;
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private AlbumPageService albumPageService;
	@Autowired
	private Jpa2ndLevelCacheUtils cacheUtils;
	@Autowired
	private ImageQueryRepository imageQueryRepository;

	@BeforeAll
	void beforeAll() throws IOException {
		jsonDir = Files.createDirectory(tempDir.resolve("json"));
	}

	@BeforeEach
	void setup(WebApplicationContext wac) {
		this.cacheUtils.evictAll();
		this.albumRepository.deleteAll();
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		this.defaultAlbumsRoot();
		this.photosJsonPath(jsonDir);
		this.photosPerPage(PHOTOS_PER_PAGE);
	}

	@WithMockUser(value = "admin", roles = {"ADMIN"})
	@Test
	void reImportAll() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(
				post("/json/import/reImport")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(instanceOf(Map.class)))
				.andReturn();

		this.mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message")
						.value("Reimported albums: " +
								String.join(", ", List.of(CASA_URLUIENI, SIMFONIA_LALELELOR))));

		// waiting for AlbumExporterSubscription (writeJsonForAlbumSafe)
		safeSleep(2000L, "reImportAll");

		List.of(CASA_URLUIENI, SIMFONIA_LALELELOR).forEach(sneaked(this::verifyAlbum));

		log.debug("END");
	}

	@WithMockUser(value = "admin", roles = {"ADMIN"})
	@Test
	void reImportNone() throws Exception {
		this.albumsRoot(Files.createDirectory(tempDir.resolve("reImportNone")));

		MvcResult mvcResult = this.mockMvc.perform(
				post("/json/import/reImport")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(instanceOf(Map.class)))
				.andReturn();

		this.mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("Reimported albums: none"));

		// waiting for AlbumExporterSubscription (writeJsonForAlbumSafe)
		safeSleep(2000L, "reImportNone");

		assertTrue(this.albumRepository.findAll().isEmpty());

		log.debug("END");
	}

	@WithMockUser(value = "admin", roles = {"ADMIN"})
	@Test
	void reImportAllWithExisting1AlbumInDB() throws Exception {
		this.reImportExistingPath(SIMFONIA_LALELELOR);// 1 new
		this.reImportAll();// 1 new, 1 to update
		this.reImportAll();// 2 to update
	}

	@WithMockUser(value = "admin", roles = {"ADMIN"})
	@Test
	void importNewAlbumsOnly() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(
				post("/json/import/importNewAlbumsOnly")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(instanceOf(Map.class)))
				.andReturn();

		this.mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message")
						.value("Imported albums: " +
								String.join(", ", List.of(CASA_URLUIENI, SIMFONIA_LALELELOR))));

		// waiting for AlbumExporterSubscription (writeJsonForAlbumSafe)
		safeSleep(2000L, "importNewAlbumsOnly");

		List.of(CASA_URLUIENI, SIMFONIA_LALELELOR).forEach(sneaked(this::verifyAlbum));

		log.debug("END");
	}

	@WithMockUser(value = "admin", roles = {"ADMIN"})
	@Test
	void reImportMissingPath() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(
				post("/json/import/reImport")
						.content(this.mapper.writeValueAsString(
								new JsonStringValue(MISSING_ALBUM)))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(instanceOf(Map.class)))
				.andReturn();

		this.mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message")
						.value("Reimported album: " + MISSING_ALBUM + " failed"));

		log.debug("END");
	}

	@WithMockUser(value = "admin", roles = {"ADMIN"})
	@ParameterizedTest
	@ValueSource(strings = SIMFONIA_LALELELOR)
	void reImportExistingPath(String albumToReimport) throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(
				post("/json/import/reImport")
						.content(this.mapper.writeValueAsString(
								new JsonStringValue(albumToReimport)))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(instanceOf(Map.class)))
				.andReturn();

		this.mockMvc.perform(asyncDispatch(mvcResult))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message")
						.value("Reimported album: " + albumToReimport));

		safeSleep(2000L, "reImportExistingPath");

		this.verifyAlbum(albumToReimport);

		log.debug("END");
	}

	private void verifyAlbum(String name) throws IOException {
		Album album = this.albumRepository.findByName(name);
		assertNotNull(album);

		// check that all album's available images were imported
		assertEquals(COUNT.get(name), this
				.imageQueryRepository.countByAlbum_Id(album.getId()));

		// read 1th asc json as List<AlbumPage>
		List<AlbumPage> albumPages = this.fileStoreService.readJsonAsList(
				jsonDir.resolve(album.getId().toString()).resolve("asc1.json"), new TypeReference<>() {});
		assertThat(albumPages, hasSize(PHOTOS_PER_PAGE));

		// compare 1th asc json to albumPageService.getPage(1, ASC, null, null, albumId)
		this.albumPageService.getPage(1, ESortType.ASC, null, false, false, album.getId())
				.forEach(fromDbAlbumPage -> {
					// 20120.01.05: AlbumPage.thumbLastModified is excluded from serialization to JSON!
					fromDbAlbumPage.setThumbLastModified(null);
					assertThat(albumPages, hasItem(fromDbAlbumPage));
				});
	}
}
