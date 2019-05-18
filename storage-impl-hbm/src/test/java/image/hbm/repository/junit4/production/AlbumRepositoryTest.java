package image.hbm.repository.junit4.production;

import image.hbm.repository.springconfig.HbmProdJdbcDbConfig;
import image.persistence.entity.Album;
import image.persistence.repository.AlbumRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasItem;

/**
 * Created by adr on 2/23/18.
 */
@RunWith(SpringRunner.class)
@HbmProdJdbcDbConfig
@Category(HbmProdJdbcDbConfig.class)
public class AlbumRepositoryTest {
	private static final Logger logger = LoggerFactory.getLogger(AlbumRepositoryTest.class);

	@Autowired
	private AlbumRepository albumRepository;

	@Test
	public void getAlbumsOrderedByName() throws Exception {
		List<Album> albums = this.albumRepository.findByDeletedFalseOrderByNameDesc();
		assertThat(albums, hasItem(anything()));
		logger.debug("albums.size = {}", albums.size());
	}

	@Test
	public void getAlbumById() throws Exception {
		Album album = this.albumRepository.getById(1);
		Assert.assertNotNull(album);
		logger.debug("albums:\n{}", album.toString());
	}

	@Test
	public void findAlbumByName() throws Exception {
		Album album = this.albumRepository.findAlbumByName("2011-08-19-Gradac");
		Assert.assertNotNull(album);
		logger.debug("albums:\n{}", album.toString());
	}
}
