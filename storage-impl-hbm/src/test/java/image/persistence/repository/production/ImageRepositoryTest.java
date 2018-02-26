package image.persistence.repository.production;

import image.persistence.HibernateConfig;
import image.persistence.repository.springtestconfig.ProdJdbcDsTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by adr on 2/25/18.
 */
@RunWith(SpringRunner.class)
@NotThreadSafe
@ProdJdbcDsTestConfig
@Category(HibernateConfig.class)
public class ImageRepositoryTest {
	@Test
	public void getImagesByAlbumId() throws Exception {

	}
}