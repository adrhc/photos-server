package image.persistence.repository.production;

import image.cdm.album.page.AlbumPage;
import image.persistence.HibernateConfig;
import image.persistence.repository.AlbumPageRepository;
import image.persistence.repository.ESortType;
import image.persistence.repository.springtestconfig.ProdJdbcDsTestConfig;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * Created by adrianpetre on 23.02.2018.
 */
@RunWith(SpringRunner.class)
@NotThreadSafe
@ProdJdbcDsTestConfig
@Category(HibernateConfig.class)
public class AlbumPageRepositoryTest {
	private static final Logger logger = LoggerFactory.getLogger(AlbumPageRepositoryTest.class);

	private static final String T1_TO_SEARCH = "DSC_1555";
	private static final Integer T1_ALBUM_ID = null;
	private static final String T2_TO_SEARCH = "DSC_1800";
	private static final Integer T2_ALBUM_ID = 1;

	@Inject
	private AlbumPageRepository albumPageRepository;

	@Test
	public void getPageCount() {
		int pageCount = this.albumPageRepository.getPageCount(
				T1_TO_SEARCH, false, false, T1_ALBUM_ID);
		logger.debug("imageCount = {}, searching {}, hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}",
				pageCount, T1_TO_SEARCH, T1_ALBUM_ID);
		Assert.assertTrue(pageCount == 1);
		pageCount = this.albumPageRepository.getPageCount(
				T2_TO_SEARCH, true, false, T2_ALBUM_ID);
		logger.debug("imageCount = {}, searching {}, hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}",
				pageCount, T2_TO_SEARCH, T2_ALBUM_ID);
		Assert.assertTrue(pageCount == 1);
	}

	@Test
	public void getPageFromDb() {
		List<AlbumPage> imagesForPage = this.albumPageRepository.getPageFromDb(1,
				ESortType.ASC, T1_TO_SEARCH, false, false, T1_ALBUM_ID);
		logger.debug("imagesForPage.size = {}, sort ASC, searching {}, hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}", imagesForPage.size(),
				T1_TO_SEARCH, T1_ALBUM_ID);
		assertThat(imagesForPage, hasSize(2));
		imagesForPage = this.albumPageRepository.getPageFromDb(1,
				ESortType.ASC, T2_TO_SEARCH, false, false, T2_ALBUM_ID);
		logger.debug("imagesForPage.size = {}, sort ASC, searching {}, hidden = false, " +
						"viewOnlyPrintable = false, albumId = {}", imagesForPage.size(),
				T2_TO_SEARCH, T2_ALBUM_ID);
		assertThat(imagesForPage, hasSize(1));
	}
}
