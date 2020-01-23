package image.photostests.junit4.misc;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static exifweb.util.file.ClassPathUtils.pathOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assume.assumeTrue;

@Category(MiscTestCategory.class)
@Slf4j
public class IdentifyPictureTest {
	private static final String ITENTIFY = "/usr/bin/identify";
	private static final Path IMAGE = pathOf("classpath:images/20171105_130105.jpg");

	@Before
	public void beforeEach() {
		assumeTrue("missing " + ITENTIFY, Files.isExecutable(Path.of(ITENTIFY)));
	}

	@Test
	public void identifyPictureTest() throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder(ITENTIFY, IMAGE.toString());
		Process exec = processBuilder.start();
		InputStream is = exec.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String sCurrentLine = br.readLine();
		log.debug(sCurrentLine);
		assertThat(sCurrentLine, not(emptyOrNullString()));
		assertThat(sCurrentLine,
				containsString("20171105_130105.jpg JPEG 1152x2048 1152x2048+0+0 8-bit sRGB"));
	}
} 
