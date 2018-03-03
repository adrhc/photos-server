package image.photos.album;

import image.cdm.album.cover.AlbumCover;
import image.photos.springtestconfig.ProdJdbcDsPhotosTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by adr on 2/21/18.
 */
@RunWith(SpringRunner.class)
@NotThreadSafe
@ProdJdbcDsPhotosTestConfig
@Category(ProdJdbcDsPhotosTestConfig.class)
public class AlbumCoverServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverServiceTest.class);

	@Autowired
	private AlbumCoverService albumCoverService;

	@Test
	public void getCoverById() {
		AlbumCover cover = this.albumCoverService.getCoverById(45);
		assertThat(cover, notNullValue());
		logger.debug(cover.getAlbumName());
	}

	@Test
	public void getCoverByName() {
		AlbumCover cover = this.albumCoverService.getCoverByName("2015-08-23 Natalia");
		assertThat(cover, notNullValue());
		logger.debug("{}, pk = {}", cover.getAlbumName(), cover.getId());
	}

	@Test
	public void getCovers() {
		List<AlbumCover> covers = this.albumCoverService.getCovers();
		assertThat(covers, hasItem(anything()));
		logger.debug("covers.size = {}", covers.size());
//		logger.debug(covers.stream().map(AlbumCover::getAlbumName)
//				.collect(Collectors.joining("\n")));
	}
}
