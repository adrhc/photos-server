package image.photos.junit4.appconfig;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import image.persistence.entity.AppConfig;
import image.photos.JsonMapperConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasItem;

/**
 * Created by adr on 2/21/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = JsonMapperConfig.class)
@Category(JsonMapperConfig.class)
public class AppConfigJsonTest {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigJsonTest.class);

	@Inject
	private ObjectMapper mapper;

	@Test
	public void decodeAppConfigsJson() throws IOException {
		List<AppConfig> appConfigs = this.mapper.readValue("[{\"id\":1,\"name\":\"albums_path\",\"value\":\"/home/adr/Pictures/FOTO Daniela & Adrian jpeg/albums\"},{\"id\":2,\"name\":\"photos_per_page\",\"value\":\"120\"},{\"id\":3,\"name\":\"httpd_restart_logs\",\"value\":\"/ffp/home/root\"},{\"id\":4,\"name\":\"linux_process_status_lines_limit\",\"value\":\"10\"},{\"id\":5,\"name\":\"proc_stats_refresh_seconds\",\"value\":\"0\"},{\"id\":7,\"name\":\"sync_cpu_mem_full\",\"value\":\"true\"},{\"id\":8,\"name\":\"stop_httpd_checking\",\"value\":\"true\"},{\"id\":9,\"name\":\"subtitles-extractor-lines\",\"value\":\"13\"},{\"id\":10,\"name\":\"subtitles-extractor.log\",\"value\":\"/home/adr/subtitles-extractor.log\"},{\"id\":11,\"name\":\"subtitles-extractor-visible-lines\",\"value\":\"13\"},{\"id\":12,\"name\":\"apache-log-dir\",\"value\":\"/home/adr/apps/log\"},{\"id\":13,\"name\":\"apache-log-lines\",\"value\":\"15\"},{\"id\":14,\"name\":\"video root folder\",\"value\":\"/home/adr/Videos/\"},{\"id\":15,\"name\":\"memory progress bar style intervals\",\"value\":\"40,60,70\"},{\"id\":16,\"name\":\"cpu progress bar style intervals\",\"value\":\"10,20,50\"},{\"id\":17,\"name\":\"photos json FS path\",\"value\":\"/home/adr/apps/opt/apache-htdocs/photos/json\"},{\"id\":18,\"name\":\"use json files\",\"value\":\"true\"},{\"id\":19,\"name\":\"use json files for config\",\"value\":\"true\"},{\"id\":20,\"name\":\"cpu summary: use nsa310 CGI\",\"value\":\"false\"},{\"id\":21,\"name\":\"cpu summary: use sum on top command\",\"value\":\"true\"},{\"id\":22,\"name\":\"cpu summary: use sum on ps ax command\",\"value\":\"true\"},{\"id\":23,\"name\":\"cpu summary: use sum on ps x command\",\"value\":\"true\"},{\"id\":24,\"name\":\"cpu summary: use top (summary portion) command\",\"value\":\"true\"}]", new TypeReference<List<AppConfig>>() {});
		assertThat(appConfigs, hasItem(anything()));
		logger.debug("appConfigs.size = {}", appConfigs.size());
//        logger.debug(appConfigs.stream().map(AppConfig::toString)
//                .collect(Collectors.joining("\n")));
	}
}
