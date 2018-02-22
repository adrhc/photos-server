package image.persistence.integration.repository;

import image.persistence.HibernateConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.exparity.hamcrest.date.DateMatchers.sameOrBefore;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by adr on 2/22/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {HibernateConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=false")
@ActiveProfiles({"jdbc-ds"})
public class AlbumCoverRepositoryTest {
	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverRepositoryTest.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	@Autowired
	private AlbumCoverRepository albumCoverRepository;

	@Test
	public void getAlbumCoversLastUpdateDate() {
		Date date = albumCoverRepository.getAlbumCoversLastUpdateDate();
		assertThat(date, sameOrBefore(new Date()));
		logger.debug("albumCoversLastUpdateDate: {}", sdf.format(date));
	}
}
