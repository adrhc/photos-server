package image.photos.misc;

import image.photos.util.process.ProcessRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assume.assumeTrue;

@Category(MiscTestCategory.class)
public class ImageDimensionsTest {
    private static final Logger logger = LoggerFactory.getLogger(ImageDimensionsTest.class);

    private static final String X_SH = "/home/adr/x.sh";
    private static final String IMAGE = "/home/adr/Pictures/FOTO Daniela & Adrian jpeg/albums/2017-10-14 Family/20171105_130105.jpg";

    private ProcessRunner processRunner = new ProcessRunner();

    @Before
    public void beforeMethod() {
        assumeTrue(Files.isRegularFile(Paths.get(X_SH)));
        assumeTrue(Files.isRegularFile(Paths.get(IMAGE)));
    }

    @Test
    public void prepareImageDimensions() throws IOException, InterruptedException {
//            ProcessBuilder identifyImgDimensions = new ProcessBuilder(
//                    "identify", "-format", "%[fx:w]x%[fx:h]", path);
//            ProcessBuilder identifyImgDimensions = new ProcessBuilder(
//                    "identify", "-verbose", path);
        ProcessBuilder identifyImgDimensions = new ProcessBuilder(
                X_SH, "image_dims", IMAGE);
        String dimensions = processRunner.getProcessOutput(identifyImgDimensions);
        logger.debug("dimensions " + dimensions + " for:\n" + IMAGE);
        assertThat(dimensions, not(isEmptyOrNullString()));
        String[] dims = dimensions.split("[x ]");
        assertThat(dims.length, is(2));
        logger.debug(StringUtils.arrayToCommaDelimitedString(dims));
    }
}
