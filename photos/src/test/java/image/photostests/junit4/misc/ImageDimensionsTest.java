package image.photostests.junit4.misc;

import image.photos.util.process.ProcessRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static exifweb.util.file.ClassPathUtils.pathOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

@Category(MiscTestCategory.class)
public class ImageDimensionsTest {
	private static final Logger logger = LoggerFactory.getLogger(ImageDimensionsTest.class);

	private static final String XSH = "/home/adr/x.sh";
	private static final Path IMAGE = pathOf("classpath:images/20171105_130105.jpg");

	private ProcessRunner processRunner = new ProcessRunner();

	@Before
	public void beforeMethod() {
		assumeTrue("missing " + XSH, Files.isExecutable(Path.of(XSH)));
	}

	@EnabledIf("#{systemProperties['os.name'].toLowerCase().contains('linux')}")
	@Test
	public void prepareImageDimensions() throws IOException, InterruptedException {
//            ProcessBuilder identifyImgDimensions = new ProcessBuilder(
//                    "identify", "-format", "%[fx:w]x%[fx:h]", path);
//            ProcessBuilder identifyImgDimensions = new ProcessBuilder(
//                    "identify", "-verbose", path);
		ProcessBuilder identifyImgDimensions = new ProcessBuilder(
				XSH, "image_dims", IMAGE.toString());
		String dimensions = this.processRunner.getProcessOutput(identifyImgDimensions);
		logger.debug("dimensions " + dimensions + " for:\n" + IMAGE);
		assertThat(dimensions, not(emptyOrNullString()));
		String[] dims = dimensions.split("[x ]");
		assertThat(dims.length, is(2));
		assertEquals(dims[0], "1152");
		assertEquals(dims[1], "2048");
		logger.debug(StringUtils.arrayToCommaDelimitedString(dims));
	}
}
