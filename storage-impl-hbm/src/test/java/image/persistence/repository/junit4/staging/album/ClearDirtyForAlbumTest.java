package image.persistence.repository.junit4.staging.album;

import image.persistence.entity.Album;
import image.persistence.repository.springtestconfig.HbmStagingJdbcDsTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by adr on 2/26/18.
 */
@NotThreadSafe
@HbmStagingJdbcDsTestConfig
@Category(HbmStagingJdbcDsTestConfig.class)
public class ClearDirtyForAlbumTest extends AlbumRepoWriteTestBase {
	@Override
	@Before
	@Transactional
	public void createAnAlbumWithImage() {
		super.createAnAlbumWithImage();
		this.album.setDirty(true);
	}

	@Test
	public void clearDirtyForAlbum() throws Exception {
		boolean result = this.albumRepository.clearDirtyForAlbum(this.album.getId());
		Assert.assertTrue(result);
		Album alteredAlbum = this.albumRepository.getAlbumById(this.album.getId());
		Assert.assertFalse(alteredAlbum.isDirty());
	}
}
