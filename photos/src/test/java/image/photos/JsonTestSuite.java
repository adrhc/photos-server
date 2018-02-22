package image.photos;

import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Filtering AllTestsSuite by JsonMapperConfig category.
 * <p>
 * Created by adrianpetre on 22.02.2018.
 */
@RunWith(Categories.class)
@Categories.IncludeCategory(JsonMapperConfig.class)
//@Suite.SuiteClasses({AppConfigJsonTest.class, ExifInfoJsonTest.class, AlbumPageJsonTest.class})
@Suite.SuiteClasses(AllTestsSuite.class)
public class JsonTestSuite {
}
