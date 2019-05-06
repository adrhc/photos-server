package image.exifwebtests.jsp;

import image.exifwebtests.config.WebInMemoryDbConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebInMemoryDbConfig
class IndexJspControllerTest {
	private MockMvc mockMvc;

	@BeforeEach
	void setup(WebApplicationContext wac) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	void indexJsp() throws Exception {
		this.mockMvc.perform(get("/index-jsp"))
				.andExpect(view().name("index"));
	}
}
