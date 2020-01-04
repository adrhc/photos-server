package image.exifwebtests.page;

import image.exifwebtests.config.WebInMemoryDbFilledConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * JsonPath WIKI: https://github.com/json-path/JsonPath
 */
@WebInMemoryDbFilledConfig
@Tag("controller")
class AlbumPageCtrlIT {
	private MockMvc mockMvc;

	@BeforeAll
	void setup(WebApplicationContext wac) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@WithMockUser(value = "admin", roles = {"ADMIN"})
	@Test
	void getAlbumPage() throws Exception {
		this.mockMvc.perform(get("/json/page")
				.accept(MediaType.APPLICATION_JSON)
				.param("albumId", "1")
				.param("pageNr", "1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0]").isMap())
				.andExpect(jsonPath("$[0].hidden").value(false));
	}
}
