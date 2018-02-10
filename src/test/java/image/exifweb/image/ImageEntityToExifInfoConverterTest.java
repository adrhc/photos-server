package image.exifweb.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import image.exifweb.image.dto.ExifInfo;
import image.exifweb.system.persistence.entities.Image;
import image.exifweb.util.frameworks.hibernate.HibernateAwareObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by adr on 2/10/18.
 */
public class ImageEntityToExifInfoConverterTest {
	private static final Logger logger =
			LoggerFactory.getLogger(ImageEntityToExifInfoConverterTest.class);

	/**
	 * https://www.mkyong.com/hibernate/java-lang-classformaterror-absent-code-attribute-in-method-that-is-not-native-or-abstract-in-class-file/
	 *
	 * @throws IOException
	 */
	@Test
	public void convert() throws IOException {
		/**
		 * see also image.exifweb.util.frameworks.hibernate.HibernateAwareObjectMapper
		 */
		ObjectMapper mapper = new HibernateAwareObjectMapper();
		Image image = mapper.readValue("{\"@id\":\"c07bb039-9ff2-4be0-b0bb-844892f2e025\",\"id\":20567,\"name\":\"DSC_3814.jpg\",\"imageMetadata\":{\"dateTime\":\"07.02.2018 22:37:57\",\"thumbLastModified\":\"05.02.2018 23:35:10\",\"exifData\":{\"imageHeight\":1268,\"imageWidth\":1920,\"apertureValue\":null,\"contrast\":null,\"dateTimeOriginal\":\"07.02.2018 22:37:57\",\"lensModel\":null,\"meteringMode\":null,\"model\":null,\"saturation\":null,\"sceneCaptureType\":null,\"sharpness\":null,\"shutterSpeedValue\":null,\"subjectDistanceRange\":null,\"whiteBalanceMode\":null,\"exposureBiasValue\":null,\"exposureMode\":null,\"exposureProgram\":null,\"exposureTime\":null,\"fNumber\":null,\"flash\":null,\"focalLength\":null,\"gainControl\":null,\"isoSpeedRatings\":0}},\"status\":16,\"deleted\":false,\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":true,\"rating\":3,\"album\":null,\"lastUpdate\":\"10.02.2018\"}", Image.class);
		logger.debug(image.toString());
		ImageMetadataEntityToDTOConverter converter = new ImageMetadataEntityToDTOConverter();
		ExifInfo exifInfo = converter.convert(image);
		logger.debug(exifInfo.toString());
	}
}
