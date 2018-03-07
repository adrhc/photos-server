package image.persistence.repository.junit4.production;

import image.persistence.entity.Image;
import image.persistence.repository.ImageRepository;
import image.persistence.repository.springtestconfig.HbmProdJdbcDsTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;

/**
 * Created by adr on 2/25/18.
 */
@RunWith(SpringRunner.class)
@NotThreadSafe
@HbmProdJdbcDsTestConfig
@Category(HbmProdJdbcDsTestConfig.class)
public class ImageRepositoryTest {
	@Autowired
	private ImageRepository imageRepository;

	@Test
	public void getImagesByAlbumId() throws Exception {
		List<Image> images = this.imageRepository.getImagesByAlbumId(1);
		assertThat(images, hasItem(anything()));
	}
}