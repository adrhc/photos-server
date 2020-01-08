package image.exifwebtests.album.importer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import image.cdm.album.page.AlbumPage;
import image.exifweb.web.json.JsonStringValue;
import image.exifwebtests.app.AppConfigFromClassPath;
import image.exifwebtests.config.WebInMemoryDbConfig;
import image.jpa2x.repositories.AlbumRepository;
import image.persistence.entity.Album;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.repository.ESortType;
import image.photos.album.services.AlbumPageService;
import image.photos.infrastructure.filestore.FileStoreService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static exifweb.util.concurrency.ThreadUtils.safeSleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebInMemoryDbConfig
@Tag("controller")
class AlbumImporterCtrlIT extends AppConfigFromClassPath {
	private static final String SIMFONIA_LALELELOR = "2013-04-20_Simfonia_lalelelor";
	private static final String MISSING_ALBUM = "MISSING ALBUM";
	private static final int PHOTOS_PER_PAGE = 5;
	/**
	 * 2.20.1. The TempDirectory Extension
	 * <p>
	 * If you wish to retain a single reference to a temp directory across lifecycle methods and the current test method, please use field injection, by annotating a non-private instance field with @TempDir.
	 */
	@TempDir
	static Path tempDir;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private FileStoreService fileStoreService;
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private AlbumPageService albumPageService;
	private MockMvc mockMvc;

	@BeforeAll
	void setup(WebApplicationContext wac) {
		super.setupWithTempDir(tempDir);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		this.saveConfig(String.valueOf(PHOTOS_PER_PAGE), AppConfigEnum.photos_per_page);
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
						.value("imported albums: " + SIMFONIA_LALELELOR));

		// waiting for AlbumExporterSubscription (writeJsonForAlbumSafe)
		safeSleep(2000);

		// load album from DB
		Album album = this.albumRepository.findByName(SIMFONIA_LALELELOR);
		assertNotNull(album);

		// read 1th asc json as List<AlbumPage>
		List<AlbumPage> albumPages = this.fileStoreService.readJsonAsList(tempDir
				.resolve(album.getId().toString()).resolve("asc1.json"), new TypeReference<>() {});
		assertThat(albumPages, hasSize(PHOTOS_PER_PAGE));

		// compare 1th asc json to albumPageService.getPage(1, ASC, null, null, albumId)
		this.albumPageService.getPage(1, ESortType.ASC, null, false, false, album.getId())
				.forEach(fromDbAlbumPage -> {
					// 20120.01.05: AlbumPage.thumbLastModified is excluded from serialization to JSON!
					fromDbAlbumPage.setThumbLastModified(null);
					assertThat(albumPages, hasItem(fromDbAlbumPage));
				});
	}

	@WithMockUser(value = "admin", roles = {"ADMIN"})
	@Test
	void importMissingAlbum() throws Exception {
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
	}
}
