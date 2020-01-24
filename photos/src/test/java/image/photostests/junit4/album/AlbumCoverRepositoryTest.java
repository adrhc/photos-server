package image.photostests.junit4.album;

import image.cdm.album.cover.AlbumCover;
import image.jpa2x.repositories.album.AlbumCoverRepository;
import image.photostests.junit4.testconfig.PhotosProdJdbcDbConfig;
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
@PhotosProdJdbcDbConfig
@Category(PhotosProdJdbcDbConfig.class)
public class AlbumCoverRepositoryTest {
	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverRepositoryTest.class);

	@Autowired
	private AlbumCoverRepository albumCoverRepository;

	@Test
	public void getCovers() {
		List<AlbumCover> covers = this.albumCoverRepository.getCovers();
		assertThat(covers, hasItem(anything()));
		logger.debug("covers.size = {}", covers.size());
	}

	@Test
	public void getCoverById() {
		AlbumCover cover = this.albumCoverRepository.getCoverById(45);
		assertThat(cover, notNullValue());
		logger.debug(cover.getAlbumName());
	}

	@Test
	public void getCoverByName() {
		AlbumCover cover = this.albumCoverRepository.getCoverByName("2015-08-23 Natalia");
		assertThat(cover, notNullValue());
		logger.debug("{}, pk = {}", cover.getAlbumName(), cover.getId());
	}
}
