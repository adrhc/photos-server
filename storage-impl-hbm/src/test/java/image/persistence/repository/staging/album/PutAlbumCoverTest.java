package image.persistence.repository.staging.album;

import image.persistence.entity.Album;
import image.persistence.repository.springtestconfig.TestJdbcDsTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Created by adr on 2/23/18.
 */
@NotThreadSafe
@TestJdbcDsTestConfig
@Category(TestJdbcDsTestConfig.class)
public class PutAlbumCoverTest extends AlbumRepoWriteTestBase {
	@Test
	public void putAlbumCover() throws Exception {
		boolean result = this.albumRepository.putAlbumCover(this.image.getId());
		Assert.assertTrue(result);
		Album alteredAlbum = this.albumRepository.getAlbumById(this.album.getId());
		Assert.assertEquals(alteredAlbum.getCover().getId(), this.image.getId());
		logger.debug("cover set using image.id = {}, image.name = {}",
				this.image.getId(), this.image.getName());
	}
}
