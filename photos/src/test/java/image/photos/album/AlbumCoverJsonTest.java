package image.photos.album;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import image.cdm.album.cover.AlbumCover;
import image.photos.JsonMapperConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * Created by adr on 2/21/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = JsonMapperConfig.class)
@Category(JsonMapperConfig.class)
public class AlbumCoverJsonTest {
	private static final Logger logger = LoggerFactory.getLogger(AlbumCoverJsonTest.class);

	@Inject
	private ObjectMapper mapper;

	@Test
	public void decodeAlbumCoversJson() throws IOException {
		String json = "[{\"id\":63,\"albumName\":\"2017-12-16 Telefon Daniela\",\"imgName\":\"IMG_20171107_163634.jpg\",\"imageHeight\":88,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2017-12-16 Telefon Daniela/1513450907000/IMG_20171107_163634.jpg\"},{\"id\":62,\"albumName\":\"2017-10-14 Family\",\"imgName\":\"DSC_8415.jpg\",\"imageHeight\":103,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2017-10-14 Family/1511088090000/DSC_8415.jpg\"},{\"id\":60,\"albumName\":\"2017-09-01 Family\",\"imgName\":\"IMG_1455.JPG\",\"imageHeight\":117,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2017-09-01 Family/1504470814000/IMG_1455.JPG\"},{\"id\":59,\"albumName\":\"2017-07-01 Serbare 1 Iunie\",\"imgName\":\"  (330).JPG\",\"imageHeight\":157,\"imageWidth\":104,\"dirty\":false,\"thumbPath\":\"thumbs/2017-07-01 Serbare 1 Iunie/1500679040000/  (330).JPG\"},{\"id\":57,\"albumName\":\"2017-07-01 Family\",\"imgName\":\"IMG_1225.JPG\",\"imageHeight\":157,\"imageWidth\":117,\"dirty\":false,\"thumbPath\":\"thumbs/2017-07-01 Family/1498931913000/IMG_1225.JPG\"},{\"id\":61,\"albumName\":\"2017-07-01 Ami 1 Iunie\",\"imgName\":\"DSC_8306.jpg\",\"imageHeight\":103,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2017-07-01 Ami 1 Iunie/1508003944000/DSC_8306.jpg\"},{\"id\":56,\"albumName\":\"2017-05-20 Family\",\"imgName\":\"IMG_20170306_132151.jpg\",\"imageHeight\":88,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2017-05-20 Family/1495270125000/IMG_20170306_132151.jpg\"},{\"id\":55,\"albumName\":\"2017-05-13 Mot Maria Madalina\",\"imgName\":\"20170513_165319.jpg\",\"imageHeight\":157,\"imageWidth\":88,\"dirty\":false,\"thumbPath\":\"thumbs/2017-05-13 Mot Maria Madalina/1495047060000/20170513_165319.jpg\"},{\"id\":54,\"albumName\":\"2017-05-01 Gradina Botanica\",\"imgName\":\"DSC_8191.jpg\",\"imageHeight\":103,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2017-05-01 Gradina Botanica/1494172948000/DSC_8191.jpg\"},{\"id\":53,\"albumName\":\"2016-11-26 Ziua lui Marc\",\"imgName\":\"IMG_0760.JPG\",\"imageHeight\":117,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2016-11-26 Ziua lui Marc/1480249672000/IMG_0760.JPG\"},{\"id\":51,\"albumName\":\"2016-11-06 Party Amalia\",\"imgName\":\"DSC_8043.jpg\",\"imageHeight\":103,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2016-11-06 Party Amalia/1478591517000/DSC_8043.jpg\"},{\"id\":52,\"albumName\":\"2016-11-05 Gradina zoologica\",\"imgName\":\"WhatsApp Image 2016-11-06 at 22.49.17.jpeg\",\"imageHeight\":157,\"imageWidth\":88,\"dirty\":false,\"thumbPath\":\"thumbs/2016-11-05 Gradina zoologica/1478546386000/WhatsApp Image 2016-11-06 at 22.49.17.jpeg\"},{\"id\":50,\"albumName\":\"2016-09-01 Gradinita\",\"imgName\":\"WhatsApp Image 2016-09-09 at 12.14.36.jpeg\",\"imageHeight\":157,\"imageWidth\":88,\"dirty\":false,\"thumbPath\":\"thumbs/2016-09-01 Gradinita/1473412806000/WhatsApp Image 2016-09-09 at 12.14.36.jpeg\"},{\"id\":48,\"albumName\":\"2016-08-20 Concediu Bulgaria\",\"imgName\":\"DSC_7554.jpg\",\"imageHeight\":103,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2016-08-20 Concediu Bulgaria/1469514147000/DSC_7554.jpg\"},{\"id\":49,\"albumName\":\"2016-08-15 Tata si mama in USA\",\"imgName\":\"SAM_0927.JPG\",\"imageHeight\":88,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2016-08-15 Tata si mama in USA/1472575473000/SAM_0927.JPG\"},{\"id\":47,\"albumName\":\"2015-12-01 Family\",\"imgName\":\"IMG_1076.JPG\",\"imageHeight\":117,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2015-12-01 Family/1499073996000/IMG_1076.JPG\"},{\"id\":46,\"albumName\":\"2015-10-24 Botez Nataly\",\"imgName\":\"DSC_1501.JPG\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2015-10-24 Botez Nataly/1447013566000/DSC_1501.JPG\"},{\"id\":45,\"albumName\":\"2015-08-23 Natalia\",\"imgName\":\"DSC_6932.jpg\",\"imageHeight\":133,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2015-08-23 Natalia/1447014048000/DSC_6932.jpg\"},{\"id\":44,\"albumName\":\"2015-07-15 Concediu Tata si Mama\",\"imgName\":\"SAM_0525.JPG\",\"imageHeight\":88,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2015-07-15 Concediu Tata si Mama/1446976056000/SAM_0525.JPG\"},{\"id\":43,\"albumName\":\"2015-06-15 Concediu Grecia\",\"imgName\":\"DSC_6561.jpg\",\"imageHeight\":103,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2015-06-15 Concediu Grecia/1446976051000/DSC_6561.jpg\"},{\"id\":42,\"albumName\":\"2015-04-12 Paste la Tulcea\",\"imgName\":\"DSC_6412.jpg\",\"imageHeight\":103,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2015-04-12 Paste la Tulcea/1446975890000/DSC_6412.jpg\"},{\"id\":37,\"albumName\":\"2015-02-21 Alice 3 ani\",\"imgName\":\"DSC_6196.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2015-02-21 Alice 3 ani/1446976002000/DSC_6196.jpg\"},{\"id\":35,\"albumName\":\"2014-10-08 Nunta civila Marian & Gabi\",\"imgName\":\"DSC_5956.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2014-10-08 Nunta civila Marian & Gabi/1446975913000/DSC_5956.jpg\"},{\"id\":34,\"albumName\":\"2014-10-03 Italia job\",\"imgName\":\"DSC_5833.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2014-10-03 Italia job/1446975994000/DSC_5833.jpg\"},{\"id\":33,\"albumName\":\"2014-09-22 Geo & Elena in RO\",\"imgName\":\"DSC_5810.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2014-09-22 Geo & Elena in RO/1446976022000/DSC_5810.jpg\"},{\"id\":31,\"albumName\":\"2014-08-22 Concediu\",\"imgName\":\"DSC_5443.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2014-08-22 Concediu/1476696244000/DSC_5443.jpg\"},{\"id\":30,\"albumName\":\"2014-04-28 Brasov\",\"imgName\":\"DSC_4555.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2014-04-28 Brasov/1461183273000/DSC_4555.jpg\"},{\"id\":29,\"albumName\":\"2014-04-20 Paste la Tulcea\",\"imgName\":\"DSC_4131.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2014-04-20 Paste la Tulcea/1446975996000/DSC_4131.jpg\"},{\"id\":27,\"albumName\":\"2013-11-29 Nunta Rodica & Daniel\",\"imgName\":\"DSC_3258.jpg\",\"imageHeight\":129,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2013-11-29 Nunta Rodica & Daniel/1461181767000/DSC_3258.jpg\"},{\"id\":26,\"albumName\":\"2013-10-05 Nunta Daniel\",\"imgName\":\"DSC_2841.jpg\",\"imageHeight\":125,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2013-10-05 Nunta Daniel/1461183421000/DSC_2841.jpg\"},{\"id\":12,\"albumName\":\"2013-08-05_Concediu_Scarisoara\",\"imgName\":\"DSC_2650.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2013-08-05_Concediu_Scarisoara/1461185008000/DSC_2650.jpg\"},{\"id\":6,\"albumName\":\"2013-07-21_Mioveni\",\"imgName\":\"DSC_2378.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2013-07-21_Mioveni/1461182678000/DSC_2378.jpg\"},{\"id\":3,\"albumName\":\"2013-07-01_Tulcea\",\"imgName\":\"DSC_2270.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2013-07-01_Tulcea/1461182111000/DSC_2270.jpg\"},{\"id\":14,\"albumName\":\"2013-06-23_Defileul_Oltului\",\"imgName\":\"DSC_1962.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2013-06-23_Defileul_Oltului/1461178566000/DSC_1962.jpg\"},{\"id\":8,\"albumName\":\"2013-05-19_La_picnic_Mioveni\",\"imgName\":\"DSC_1498.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2013-05-19_La_picnic_Mioveni/1461183441000/DSC_1498.jpg\"},{\"id\":2,\"albumName\":\"2013-05-01_Paste_la_Tulcea\",\"imgName\":\"DSC_1073.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2013-05-01_Paste_la_Tulcea/1461182399000/DSC_1073.jpg\"},{\"id\":15,\"albumName\":\"2013-04-27_Padure\",\"imgName\":\"DSC_0482.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2013-04-27_Padure/1461183402000/DSC_0482.jpg\"},{\"id\":10,\"albumName\":\"2013-04-20_Simfonia_lalelelor\",\"imgName\":\"DSC_0331.jpg\",\"imageHeight\":157,\"imageWidth\":144,\"dirty\":false,\"thumbPath\":\"thumbs/2013-04-20_Simfonia_lalelelor/1461178054000/DSC_0331.jpg\"},{\"id\":18,\"albumName\":\"2013-02-16_Mot_Alis\",\"imgName\":\"DSC_0720.jpg\",\"imageHeight\":134,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2013-02-16_Mot_Alis/1461183059000/DSC_0720.jpg\"},{\"id\":19,\"albumName\":\"2013-01-12_Amalia_botez\",\"imgName\":\"DSC_0381.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2013-01-12_Amalia_botez/1461184635000/DSC_0381.jpg\"},{\"id\":16,\"albumName\":\"2012-11-07_Amalia\",\"imgName\":\"DSC_7045.jpg\",\"imageHeight\":103,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2012-11-07_Amalia/1447064634000/DSC_7045.jpg\"},{\"id\":20,\"albumName\":\"2012-10-20_zi_Adila_nunta_Adriana_aviatie_macro\",\"imgName\":\"DSC_9590.jpg\",\"imageHeight\":148,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2012-10-20_zi_Adila_nunta_Adriana_aviatie_macro/1461183448000/DSC_9590.jpg\"},{\"id\":25,\"albumName\":\"2012-08-05-Concediu-August-2012\",\"imgName\":\"DSC_9160.jpg\",\"imageHeight\":157,\"imageWidth\":137,\"dirty\":false,\"thumbPath\":\"thumbs/2012-08-05-Concediu-August-2012/1461183640000/DSC_9160.jpg\"},{\"id\":22,\"albumName\":\"2012-07-07-nunta-George-Elena\",\"imgName\":\"DSC_7772.jpg\",\"imageHeight\":125,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2012-07-07-nunta-George-Elena/1461183550000/DSC_7772.jpg\"},{\"id\":21,\"albumName\":\"2012-06-24-Costinesti\",\"imgName\":\"DSC_7461.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2012-06-24-Costinesti/1461183432000/DSC_7461.jpg\"},{\"id\":7,\"albumName\":\"2012-05-02-Delta\",\"imgName\":\"DSC_6309.jpg\",\"imageHeight\":157,\"imageWidth\":104,\"dirty\":false,\"thumbPath\":\"thumbs/2012-05-02-Delta/1461184246000/DSC_6309.jpg\"},{\"id\":9,\"albumName\":\"2012-01-01-Revelion\",\"imgName\":\"DSC_5670.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2012-01-01-Revelion/1461181712000/DSC_5670.jpg\"},{\"id\":5,\"albumName\":\"2011-08-26-Ceahlau\",\"imgName\":\"DSC_3714.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2011-08-26-Ceahlau/1461178962000/DSC_3714.jpg\"},{\"id\":17,\"albumName\":\"2011-08-22-Pirat\",\"imgName\":\"DSC_3410.jpg\",\"imageHeight\":157,\"imageWidth\":104,\"dirty\":false,\"thumbPath\":\"thumbs/2011-08-22-Pirat/1461184764000/DSC_3410.jpg\"},{\"id\":13,\"albumName\":\"2011-08-21-Mostar\",\"imgName\":\"DSC_3049.jpg\",\"imageHeight\":157,\"imageWidth\":133,\"dirty\":false,\"thumbPath\":\"thumbs/2011-08-21-Mostar/1461180619000/DSC_3049.jpg\"},{\"id\":4,\"albumName\":\"2011-08-20-Dubrovnic\",\"imgName\":\"DSC_2397.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2011-08-20-Dubrovnic/1461180900000/DSC_2397.jpg\"},{\"id\":1,\"albumName\":\"2011-08-19-Gradac\",\"imgName\":\"DSC_1828.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2011-08-19-Gradac/1461182069000/DSC_1828.jpg\"},{\"id\":23,\"albumName\":\"2011-08-18-Split\",\"imgName\":\"DSC_1469.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2011-08-18-Split/1461184902000/DSC_1469.jpg\"},{\"id\":24,\"albumName\":\"2011-08-16-Plitvice\",\"imgName\":\"DSC_1112.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2011-08-16-Plitvice/1461182625000/DSC_1112.jpg\"},{\"id\":11,\"albumName\":\"2011-08-14_Serbia_Zagreb\",\"imgName\":\"DSC_0780.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2011-08-14_Serbia_Zagreb/1461184489000/DSC_0780.jpg\"},{\"id\":36,\"albumName\":\"2010-07-17 Nunta Daniela & Adrian\",\"imgName\":\"IMG_0094.jpg\",\"imageHeight\":104,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2010-07-17 Nunta Daniela & Adrian/1446975926000/IMG_0094.jpg\"},{\"id\":39,\"albumName\":\"2006-01-01 TATA, MAMA 2006-2014\",\"imgName\":\"DSC00157.JPG\",\"imageHeight\":117,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2006-01-01 TATA, MAMA 2006-2014/1446976011000/DSC00157.JPG\"},{\"id\":28,\"albumName\":\"2000-01-01 Test\",\"imgName\":\"DSC_3814.jpg\",\"imageHeight\":103,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/2000-01-01 Test/1517873710000/DSC_3814.jpg\"},{\"id\":41,\"albumName\":\"1978-01-01 COPIII 1978-2004\",\"imgName\":\"124.jpg\",\"imageHeight\":157,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/1978-01-01 COPIII 1978-2004/1446976009000/124.jpg\"},{\"id\":40,\"albumName\":\"1975-01-01 TATA, MAMA 1975-2005\",\"imgName\":\"97.jpg\",\"imageHeight\":157,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/1975-01-01 TATA, MAMA 1975-2005/1446975891000/97.jpg\"},{\"id\":38,\"albumName\":\"1956-01-01 TATA, MAMA ALTII 1956-1974\",\"imgName\":\"13.jpg\",\"imageHeight\":157,\"imageWidth\":157,\"dirty\":false,\"thumbPath\":\"thumbs/1956-01-01 TATA, MAMA ALTII 1956-1974/1446976040000/13.jpg\"}]";
		List<AlbumCover> covers = mapper.readValue(json, new TypeReference<List<AlbumCover>>() {});
		assertThat(covers, hasSize(StringUtils.countOccurrencesOf(json, "{")));
		logger.debug("covers.size = {}", covers.size());
//		logger.debug(covers.stream().map(AlbumCover::getAlbumName)
//				.collect(Collectors.joining("\n")));
	}
}
