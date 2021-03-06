package image.photostests.junit4.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.cdm.image.ExifInfo;
import image.photos.JsonMapperConfig;
import image.photostests.junit4.album.AlbumPageJsonTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static image.persistence.entity.util.DateUtils.safeFormat;
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
	private static final DateTimeFormatter sdf =
			DateTimeFormatter.ofPattern(JsonMapperConfig.DATE_FORMAT).withZone(ZoneOffset.UTC);
	private static final Logger logger = LoggerFactory.getLogger(AlbumPageJsonTest.class);
	@Autowired
	private ObjectMapper mapper;

	@Test
	public void decodeExifInfoJson() throws IOException, ParseException {
		String json = "{\"id\":15828,\"name\":\"DSC_7332.jpg\",\"dateTime\":\"04.02.2018 18:16:24\",\"imageHeight\":1356,\"imageWidth\":2048,\"apertureValue\":\"F5\",\"contrast\":\"None\",\"dateTimeOriginal\":\"17.07.2016 09:18:41\",\"lensModel\":\"17.0-50.0 mm f/2.8\",\"meteringMode\":\"Center weighted average\",\"model\":\"NIKON D5100\",\"saturation\":\"None\",\"sceneCaptureType\":\"Standard\",\"sharpness\":\"None\",\"shutterSpeedValue\":\"1/1249 sec\",\"subjectDistanceRange\":\"Unknown\",\"whiteBalanceMode\":\"Auto white balance\",\"exposureBiasValue\":\"0 EV\",\"exposureMode\":\"Auto exposure\",\"exposureProgram\":\"Aperture priority\",\"exposureTime\":\"1/1250 sec\",\"fNumber\":\"F5\",\"flash\":\"Flash did not fire\",\"focalLength\":\"17.0 mm\",\"gainControl\":\"None\",\"isoSpeedRatings\":100}";
		ExifInfo exifInfo = this.mapper.readValue(json, ExifInfo.class);
		assertThat(exifInfo, notNullValue());
		// use json.dateTimeOriginal
		assertEquals(safeFormat(exifInfo.getDateTimeOriginal(), sdf), "17.07.2016 09:18:41");
		logger.debug(exifInfo.toString());
	}
}
