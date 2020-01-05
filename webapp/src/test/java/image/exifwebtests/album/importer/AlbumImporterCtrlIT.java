package image.exifwebtests.album.importer;

import image.exifwebtests.app.AppConfigFromClassPath;
import image.exifwebtests.config.WebInMemoryDbConfig;
import image.persistence.entity.enums.AppConfigEnum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Path;
import java.util.Map;

import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebInMemoryDbConfig
@Tag("controller")
class AlbumImporterCtrlIT extends AppConfigFromClassPath {
	private static final String SIMFONIA_LALELELOR = "2013-04-20_Simfonia_lalelelor";
	/**
	 * 2.20.1. The TempDirectory Extension
	 * <p>
	 * If you wish to retain a single reference to a temp directory across lifecycle methods and the current test method, please use field injection, by annotating a non-private instance field with @TempDir.
	 */
	@TempDir
	static Path tempDir;
	private MockMvc mockMvc;

	@BeforeAll
	void setup(WebApplicationContext wac) {
		super.setupWithTempDir(tempDir);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		saveConfig("5", AppConfigEnum.photos_per_page);
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
		Thread.sleep(2000);
	}
}
