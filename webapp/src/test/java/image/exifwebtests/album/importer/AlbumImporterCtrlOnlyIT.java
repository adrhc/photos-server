package image.exifwebtests.album.importer;

import image.exifweb.RootConfig;
import image.exifweb.WebConfig;
import image.exifwebtests.config.WebInMemoryDbConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static image.exifwebtests.album.importer.AlbumImporterServiceSpy.*;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebInMemoryDbConfig
@ContextHierarchy({
		@ContextConfiguration(classes = {
				RootConfig.class, AlbumImporterServiceSpy.class}),
		@ContextConfiguration(classes = {WebConfig.class})
})
@Tag("controller")
@Slf4j
public class AlbumImporterCtrlOnlyIT {
	private MockMvc mockMvc;

	@BeforeAll
	void setup(WebApplicationContext wac) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
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
						.value("Reimported albums: " + String.join(", ",
								List.of(CASA_URLUIENI, SIMFONIA_LALELELOR, MISSING_ALBUM + " failed"))));

		log.debug("END");
	}
}
