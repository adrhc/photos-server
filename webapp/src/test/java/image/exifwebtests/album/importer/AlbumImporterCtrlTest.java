package image.exifwebtests.album.importer;

import image.exifwebtests.config.WebInMemoryDbConfig;
import image.jpa2x.repositories.AppConfigRepository;
import image.persistence.entity.AppConfig;
import image.persistence.entity.enums.AppConfigEnum;
import image.persistence.entitytests.IAppConfigSupplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebInMemoryDbConfig
@Tag("controller")
class AlbumImporterCtrlTest implements IAppConfigSupplier {
	private static final String SIMFONIA_LALELELOR = "2013-04-20_Simfonia_lalelelor";
	private static final String ALBUMS_ROOT = "classpath:albums-root";
	private static final String JSON_EXPORT_ROOT = "json-root";
	private MockMvc mockMvc;
	@Autowired
	private AppConfigRepository configRepository;

	@BeforeAll
	void setup(WebApplicationContext wac) throws FileNotFoundException {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		setupAlbumsRoot();
		setupPhotosPerPage();
		setupPhotosJsonPath();
	}

	@Test
	void albumsRoot() {
		String root = configRepository.findValueByEnumeratedName(AppConfigEnum.albums_path);
		assertThat(root, endsWith(ALBUMS_ROOT.substring("classpath:".length())));
	}

	@WithMockUser(value = "ada", roles = {"ADMIN"})
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

	private void setupPhotosPerPage() {
		configRepository.save(entityAppConfigOf(AppConfigEnum.photos_per_page, "5"));
	}

	private void setupPhotosJsonPath() throws FileNotFoundException {
		Path albumsRoot = Path.of(ResourceUtils.getFile(ALBUMS_ROOT).getAbsolutePath());
		Path jsonRoot = albumsRoot.resolveSibling(JSON_EXPORT_ROOT);
		configRepository.save(entityAppConfigOf(
				AppConfigEnum.photos_json_FS_path, jsonRoot.toString()));
	}

	private void setupAlbumsRoot() throws FileNotFoundException {
		Path albumsRoot = Path.of(ResourceUtils.getFile(ALBUMS_ROOT).getAbsolutePath());
		AppConfig root = configRepository.findByEnumeratedName(AppConfigEnum.albums_path);
		if (root == null) {
			root = entityAppConfigOf(AppConfigEnum.albums_path.getValue(), albumsRoot.toString());
		} else {
			root.setValue(albumsRoot.toString());
		}
		configRepository.save(root);
	}
}
