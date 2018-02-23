package image.photos.misc;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assume.assumeTrue;

@Category(MiscTestCategory.class)
public class IdentifyPictureTest {
    private static final Logger logger = LoggerFactory.getLogger(IdentifyPictureTest.class);

    private static final String ITENTIFY = "/usr/bin/identify";
    private static final String IMAGE = "/home/adr/Pictures/FOTO Daniela & Adrian jpeg/albums/2017-10-14 Family/20171105_130105.jpg";

    @Before
    public void beforeMethod() {
        assumeTrue(Files.isRegularFile(Paths.get(ITENTIFY)));
        assumeTrue(Files.isRegularFile(Paths.get(IMAGE)));
    }

    @Test
    public void identifyPictureTest() throws IOException {
        logger.trace("PATH=" + System.getenv().get("PATH"));
        ProcessBuilder processBuilder = new ProcessBuilder(ITENTIFY, IMAGE);
        Process exec = processBuilder.start();
        InputStream is = exec.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String sCurrentLine = br.readLine();
        logger.debug(sCurrentLine);
        assertThat(sCurrentLine, not(isEmptyOrNullString()));
    }
} 
