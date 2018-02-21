package image.photos.album;

import image.cdm.album.cover.AlbumCover;
import image.photos.TestPhotosConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasItem;

/**
 * Created by adr on 2/21/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestPhotosConfig.class)
@ActiveProfiles({"integration-tests", "jdbc-ds"})
public class AlbumCoverServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverServiceTest.class);

	@Autowired
	private AlbumCoverService albumCoverService;

	@Test
	public void getCovers() throws IOException {
		List<AlbumCover> covers = albumCoverService.getCovers();
		assertThat(covers, hasItem(anything()));
		logger.debug(covers.stream().map(AlbumCover::getAlbumName)
				.collect(Collectors.joining("\n")));
	}
}
