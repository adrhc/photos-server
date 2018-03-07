package image.persistence.repository.junit4.staging.album;

import image.persistence.entity.Album;
import image.persistence.repository.springtestconfig.HbmStagingJdbcDsConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Created by adr on 2/26/18.
 */
@NotThreadSafe
@HbmStagingJdbcDsConfig
@Category(HbmStagingJdbcDsConfig.class)
public class RemoveAlbumCoverTest extends AlbumRepoWriteTestBase {
	@Override
	@Before
	public void createAnAlbumWithImage() {
		super.createAnAlbumWithImage();
		this.albumRepository.putAlbumCover(this.imageId);
	}

	@Test
	public void removeAlbumCover() throws Exception {
		boolean result = this.albumRepository.removeAlbumCover(this.imageId);
		Assert.assertTrue(result);
		Album alteredAlbum = this.albumRepository.getAlbumById(this.albumId);
		Assert.assertNull(alteredAlbum.getCover());
	}
}
