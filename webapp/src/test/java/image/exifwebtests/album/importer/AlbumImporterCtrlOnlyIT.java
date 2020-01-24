package image.exifwebtests.album.importer;

import image.exifweb.RootConfig;
import image.exifweb.WebConfig;
import image.exifwebtests.config.WebInMemoryDbConfig;
import image.infrastructure.messaging.album.AlbumEvent;
import image.infrastructure.messaging.album.AlbumEventTypeEnum;
import image.infrastructure.messaging.album.AlbumTopic;
import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.ImageQueryRepository;
import image.jpa2x.repositories.ImageRepository;
import image.persistence.entity.Album;
import image.photos.album.helpers.AlbumHelper;
import image.photos.album.helpers.AlbumPathChecks;
import image.photos.album.services.AlbumImporterService;
import image.photos.image.helpers.ImageHelper;
import image.photos.image.services.ImageImporterService;
import image.photos.infrastructure.filestore.FileStoreService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.*;

import static image.infrastructure.messaging.album.AlbumEventTypeEnum.CREATED;
import static image.infrastructure.messaging.album.AlbumEventTypeEnum.MISSING_PATH;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebInMemoryDbConfig
@ContextHierarchy({
		@ContextConfiguration(classes = {
				RootConfig.class, AlbumImporterCtrlOnlyIT.Config.class}),
		@ContextConfiguration(classes = {WebConfig.class})
})
@Tag("controller")
@Slf4j
public class AlbumImporterCtrlOnlyIT {
	public static final String SIMFONIA_LALELELOR = "2013-04-20_Simfonia_lalelelor";
	public static final String CASA_URLUIENI = "2017-07-15 Casa Urluieni";
	public static final String MISSING_ALBUM = "MISSING ALBUM";

	private MockMvc mockMvc;

	@BeforeAll
	void setup(WebApplicationContext wac) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	/**
	 * importing all albums + using a missing path
	 */
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

	static class Config {
		private static Optional<AlbumEvent> albumEvent(String name, AlbumEventTypeEnum albumEventType) {
			return Optional.of(AlbumEvent.of(new Album(name), albumEventType));
		}

		@Bean
		AlbumImporterService albumImporterService(ImageHelper imageHelper, ImageImporterService imageImporterService, ImageRepository imageRepository, ImageQueryRepository imageQueryRepository, AlbumRepository albumRepository, AlbumTopic albumTopic, AlbumPathChecks albumPathChecks, AlbumHelper albumHelper, FileStoreService fileStoreService) throws IOException {
			var bean = spy(new AlbumImporterService(imageHelper, imageImporterService,
					imageRepository, imageQueryRepository, albumRepository, albumTopic,
					albumPathChecks, albumHelper, fileStoreService));

			var albumEvents = List.of(albumEvent(CASA_URLUIENI, CREATED), albumEvent(SIMFONIA_LALELELOR, CREATED));
			var withFailed = new ArrayList<>(albumEvents);
			Collections.copy(withFailed, albumEvents);
			withFailed.add(albumEvent(MISSING_ALBUM, MISSING_PATH));

			doReturn(withFailed).when(bean).importAll();
			doReturn(albumEvent(CASA_URLUIENI, CREATED)).when(bean).importByAlbumName(anyString());
			doReturn(albumEvents).when(bean).importNewAlbums();
			return bean;
		}
	}
}
