package image.persistence.integration.repository;

import image.persistence.HibernateConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Created by adrianpetre on 23.02.2018.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {HibernateConfig.class})
@TestPropertySource(properties = "hibernate.show_sql=false")
@ActiveProfiles({"jdbc-ds"})
@Category(HibernateConfig.class)
public class AlbumPageRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(AlbumPageRepositoryTest.class);

    private static final String TO_SEARCH = "555";

    @Inject
    private AlbumPageRepository albumPageRepository;

    @Test
    public void getPageCount() {
        int pageCount = albumPageRepository.getPageCount(TO_SEARCH, false, false, null);
        logger.debug("pageCount = {}, searching {}, hidden = false, " +
                "viewOnlyPrintable = false, albumId = null", pageCount, TO_SEARCH);
        Assert.assertTrue(pageCount > 0);
    }
}
