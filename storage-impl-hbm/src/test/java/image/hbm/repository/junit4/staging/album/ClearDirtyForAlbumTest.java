package image.hbm.repository.junit4.staging.album;

import image.hbm.repository.springconfig.HbmStagingJdbcDbConfig;
import image.persistence.entity.Album;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Created by adr on 2/26/18.
 */
@HbmStagingJdbcDbConfig
@Category(HbmStagingJdbcDbConfig.class)
public class ClearDirtyForAlbumTest extends AlbumRepoWriteTestBase {
	@Autowired
	private PlatformTransactionManager transactionManager;

	@Override
	@Before
	public void createAnAlbumWithImage() {
		TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
		transactionTemplate.execute((ts) -> {
			super.createAnAlbumWithImage();
			this.album.setDirty(true);
			return null;
		});
	}

	@Test
	public void clearDirtyForAlbum() {
		boolean result = this.albumRepository.clearDirtyForAlbum(this.album.getId());
		Assert.assertTrue(result);
		Album alteredAlbum = this.albumRepository.getById(this.album.getId());
		Assert.assertFalse(alteredAlbum.isDirty());
	}
}
