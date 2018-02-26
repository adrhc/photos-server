package image.persistence.repository.staging;

import image.persistence.HibernateConfig;
import image.persistence.repository.springtestconfig.TestJdbcDsTestConfig;
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
@TestJdbcDsTestConfig
@Category(HibernateConfig.class)
public class ImageRepositoryTest {
	@Test
	public void updateThumbLastModifiedForImg() throws Exception {

	}

	@Test
	public void changeRating() throws Exception {

	}

	@Test
	public void changeStatus() throws Exception {

	}

	@Test
	public void getImagesByAlbumId() throws Exception {

	}

	@Test
	public void persistImage() throws Exception {

	}

	@Test
	public void markDeleted() throws Exception {

	}

	@Test
	public void deleteImage() throws Exception {

	}

	@Test
	public void changeName() throws Exception {

	}

}