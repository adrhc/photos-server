package image.persistence.repository.junit4.production;

import image.persistence.repository.AlbumCoverRepository;
import image.persistence.repository.springtestconfig.HbmProdJdbcDsConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.exparity.hamcrest.date.DateMatchers.sameOrBefore;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by adr on 2/22/18.
 */
@RunWith(SpringRunner.class)
@NotThreadSafe
@HbmProdJdbcDsConfig
@Category(HbmProdJdbcDsConfig.class)
public class AlbumCoverRepositoryTest {
	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverRepositoryTest.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	@Autowired
	private AlbumCoverRepository albumCoverRepository;

	@Test
	public void getAlbumCoversLastUpdateDate() {
		Date date = this.albumCoverRepository.getAlbumCoversLastUpdateDate();
		assertThat(date, sameOrBefore(new Date()));
		logger.debug("albumCoversLastUpdateDate: {}", sdf.format(date));
	}
}
