package image.hbm.repository.junit4.production;

import image.hbm.repository.springconfig.HbmProdJdbcDbConfig;
import image.persistence.repository.AlbumRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static image.persistence.entity.util.DateUtils.safeFormat;
import static org.exparity.hamcrest.date.DateMatchers.sameOrBefore;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by adr on 2/22/18.
 */
@RunWith(SpringRunner.class)
@HbmProdJdbcDbConfig
@Category(HbmProdJdbcDbConfig.class)
@Slf4j
public class AlbumCoverRepositoryTest {
	private static final DateTimeFormatter sdf =
			DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC);

	@Autowired
	private AlbumRepository albumRepository;

	@Test
	public void getAlbumCoversLastUpdateDate() {
		Date date = this.albumRepository.getAlbumCoversLastUpdateDate();
		assertThat(date, sameOrBefore(new Date()));
		log.debug("albumCoversLastUpdateDate: {}", safeFormat(date, sdf));
	}
}
