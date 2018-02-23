package image.photos.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.cdm.image.ExifInfo;
import image.photos.JsonMapperConfig;
import image.photos.album.AlbumPageJsonTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

/**
 * Created by adr on 2/21/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = JsonMapperConfig.class)
@Category(JsonMapperConfig.class)
public class ExifInfoJsonTest {
    private static final Logger logger = LoggerFactory.getLogger(AlbumPageJsonTest.class);

    @Inject
    private ObjectMapper mapper;
    private SimpleDateFormat sdf = new SimpleDateFormat(JsonMapperConfig.DATE_FORMAT);
//	private SimpleDateFormat sdf = new SimpleDateFormat(JsonMapperConfig.DATE_FORMAT) {{
//		setTimeZone(TimeZone.getTimeZone("GMT"));
//	}};

    @Test
    public void decodeExifInfoJson() throws IOException, ParseException {
        String json = "{\"id\":15828,\"name\":\"DSC_7332.jpg\",\"dateTime\":\"04.02.2018 18:16:24\",\"imageHeight\":1356,\"imageWidth\":2048,\"apertureValue\":\"F5\",\"contrast\":\"None\",\"dateTimeOriginal\":\"17.07.2016 09:18:41\",\"lensModel\":\"17.0-50.0 mm f/2.8\",\"meteringMode\":\"Center weighted average\",\"model\":\"NIKON D5100\",\"saturation\":\"None\",\"sceneCaptureType\":\"Standard\",\"sharpness\":\"None\",\"shutterSpeedValue\":\"1/1249 sec\",\"subjectDistanceRange\":\"Unknown\",\"whiteBalanceMode\":\"Auto white balance\",\"exposureBiasValue\":\"0 EV\",\"exposureMode\":\"Auto exposure\",\"exposureProgram\":\"Aperture priority\",\"exposureTime\":\"1/1250 sec\",\"fNumber\":\"F5\",\"flash\":\"Flash did not fire\",\"focalLength\":\"17.0 mm\",\"gainControl\":\"None\",\"isoSpeedRatings\":100}";
        ExifInfo exifInfo = mapper.readValue(json, ExifInfo.class);
        assertThat(exifInfo, notNullValue());
        // use json.dateTimeOriginal
        assertEquals(sdf.format(exifInfo.getDateTimeOriginal()), "17.07.2016 09:18:41");
        logger.debug(exifInfo.toString());
    }
}
