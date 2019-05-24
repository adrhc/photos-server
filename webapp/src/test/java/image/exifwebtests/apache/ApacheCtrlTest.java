package image.exifwebtests.apache;

import image.exifweb.apache.ApacheCtrl;
import image.exifwebtests.config.WebInMemoryDbFilledConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebInMemoryDbFilledConfig
@Tag("controller")
class ApacheCtrlTest {
	private MockMvc mockMvc;

	@BeforeEach
	void setup(WebApplicationContext wac) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	void notAuthorizedGetApacheLog() throws Exception {
		this.mockMvc.perform(get("/json/apache/getApacheLog")
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.param("type", ApacheCtrl.LOG_TYPE_ACCESS))
				.andExpect(status().is5xxServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.success").value("false"))
				.andExpect(jsonPath("$.error").value("true"))
				.andExpect(jsonPath("$.message").isString())
				.andExpect(jsonPath("$.['stack trace']").isString());
	}

	@WithMockUser(value = "ada", roles = {"ADMIN"})
	@Test
	void getApacheLog() throws Exception {
		this.mockMvc.perform(get("/json/apache/getApacheLog")
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.param("type", ApacheCtrl.LOG_TYPE_ACCESS))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.value").isString());
	}
}
