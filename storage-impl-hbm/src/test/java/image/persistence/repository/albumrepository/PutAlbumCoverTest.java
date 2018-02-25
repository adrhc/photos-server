package image.persistence.repository.albumrepository;

import image.persistence.HibernateConfig;
import image.persistence.repository.springtestconfig.TestJdbcDsTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Created by adr on 2/23/18.
 */
@NotThreadSafe
@TestJdbcDsTestConfig
@Category(HibernateConfig.class)
public class PutAlbumCoverTest extends AlbumRepoWriteTestBase {
	@Test
	public void putAlbumCover() throws Exception {
		boolean result = albumRepository.putAlbumCover(1);
		Assert.assertTrue(result);
		logger.debug("album cover set using image.id = 1");
	}

	@Ignore("todo: use in memory database")
	@Test
	public void removeAlbumCover() throws Exception {
		boolean result = albumRepository.removeAlbumCover(1);
		Assert.assertTrue(result);
	}

	@Ignore("todo: use in memory database")
	@Test
	public void clearDirtyForAlbum() throws Exception {
		boolean result = albumRepository.clearDirtyForAlbum(1);
		Assert.assertTrue(result);
	}
}
