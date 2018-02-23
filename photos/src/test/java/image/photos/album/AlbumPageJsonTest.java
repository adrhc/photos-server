package image.photos.album;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import image.cdm.album.page.AlbumPage;
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
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * In order not to use SpringRunner we'll have to use SpringClassRule @ClassRule and SpringMethodRule @Rule.
 * <p>
 * Created by adr on 2/21/18.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = JsonMapperConfig.class)
@Category(JsonMapperConfig.class)
public class AlbumPageJsonTest {
    private static final Logger logger = LoggerFactory.getLogger(AlbumPageJsonTest.class);

    @Inject
    private ObjectMapper mapper;

    @Test
    public void decodeAlbumPagesJson() throws IOException {
        String json = "[{\"id\":19447,\"imgName\":\"IMG-20170531-WA0001.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676881000/IMG-20170531-WA0001.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676881000/IMG-20170531-WA0001.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304810228,\"cover\":false},{\"id\":19434,\"imgName\":\"IMG-20170531-WA0012.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":117,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676864000/IMG-20170531-WA0012.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676864000/IMG-20170531-WA0012.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304811267,\"cover\":false},{\"id\":19435,\"imgName\":\"IMG-20170531-WA0011.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":117,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676865000/IMG-20170531-WA0011.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676865000/IMG-20170531-WA0011.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304811173,\"cover\":false},{\"id\":19436,\"imgName\":\"IMG-20170531-WA0003.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676868000/IMG-20170531-WA0003.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676868000/IMG-20170531-WA0003.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304810424,\"cover\":false},{\"id\":19437,\"imgName\":\"IMG-20170531-WA0014.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676869000/IMG-20170531-WA0014.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676869000/IMG-20170531-WA0014.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304811459,\"cover\":false},{\"id\":19438,\"imgName\":\"IMG-20170530-WA0013.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676871000/IMG-20170530-WA0013.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676871000/IMG-20170530-WA0013.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304809907,\"cover\":false},{\"id\":19439,\"imgName\":\"IMG-20170531-WA0018.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":88,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676872000/IMG-20170531-WA0018.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676872000/IMG-20170531-WA0018.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304811705,\"cover\":false},{\"id\":19440,\"imgName\":\"IMG-20170530-WA0000.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676873000/IMG-20170530-WA0000.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676873000/IMG-20170530-WA0000.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304808638,\"cover\":false},{\"id\":19441,\"imgName\":\"IMG-20170531-WA0024.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676876000/IMG-20170531-WA0024.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676876000/IMG-20170531-WA0024.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304812129,\"cover\":false},{\"id\":19442,\"imgName\":\"IMG-20170530-WA0002.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676877000/IMG-20170530-WA0002.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676877000/IMG-20170530-WA0002.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304808870,\"cover\":false},{\"id\":19443,\"imgName\":\"IMG-20170530-WA0005.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676878000/IMG-20170530-WA0005.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676878000/IMG-20170530-WA0005.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304809147,\"cover\":false},{\"id\":19444,\"imgName\":\"IMG-20170531-WA0013.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676879000/IMG-20170531-WA0013.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676879000/IMG-20170531-WA0013.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304811377,\"cover\":false},{\"id\":19445,\"imgName\":\"IMG-20170531-WA0000.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":117,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676880000/IMG-20170531-WA0000.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676880000/IMG-20170531-WA0000.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304810118,\"cover\":false},{\"id\":19446,\"imgName\":\"IMG-20170531-WA0022.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676881000/IMG-20170531-WA0022.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676881000/IMG-20170531-WA0022.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304812026,\"cover\":false},{\"id\":19433,\"imgName\":\"IMG-20170530-WA0012.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":88,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676863000/IMG-20170530-WA0012.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676863000/IMG-20170530-WA0012.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304809830,\"cover\":false},{\"id\":19448,\"imgName\":\"IMG-20170530-WA0008.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676882000/IMG-20170530-WA0008.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676882000/IMG-20170530-WA0008.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304809455,\"cover\":false},{\"id\":19449,\"imgName\":\"IMG-20170531-WA0015.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":117,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676884000/IMG-20170531-WA0015.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676884000/IMG-20170531-WA0015.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304811525,\"cover\":false},{\"id\":19450,\"imgName\":\"IMG-20170530-WA0003.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":88,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676885000/IMG-20170530-WA0003.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676885000/IMG-20170530-WA0003.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304808960,\"cover\":false},{\"id\":19451,\"imgName\":\"IMG-20170531-WA0021.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676886000/IMG-20170531-WA0021.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676886000/IMG-20170531-WA0021.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304811943,\"cover\":false},{\"id\":19452,\"imgName\":\"IMG-20170531-WA0008.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676887000/IMG-20170531-WA0008.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676887000/IMG-20170531-WA0008.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304810873,\"cover\":false},{\"id\":19453,\"imgName\":\"IMG-20170531-WA0017.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":88,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676888000/IMG-20170531-WA0017.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676888000/IMG-20170531-WA0017.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304811617,\"cover\":false},{\"id\":19454,\"imgName\":\"IMG-20170525-WA0014.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":94,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676889000/IMG-20170525-WA0014.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676889000/IMG-20170525-WA0014.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304808303,\"cover\":false},{\"id\":19455,\"imgName\":\"IMG-20170531-WA0006.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676890000/IMG-20170531-WA0006.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676890000/IMG-20170531-WA0006.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304810709,\"cover\":false},{\"id\":19456,\"imgName\":\"IMG-20170525-WA0010.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676892000/IMG-20170525-WA0010.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676892000/IMG-20170525-WA0010.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304808182,\"cover\":false},{\"id\":19457,\"imgName\":\"IMG-20170530-WA0001.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676893000/IMG-20170530-WA0001.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676893000/IMG-20170530-WA0001.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304808751,\"cover\":false},{\"id\":19458,\"imgName\":\"IMG-20170525-WA0015.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":94,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676894000/IMG-20170525-WA0015.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676894000/IMG-20170525-WA0015.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304808437,\"cover\":false},{\"id\":19459,\"imgName\":\"IMG-20170531-WA0004.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":117,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676896000/IMG-20170531-WA0004.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676896000/IMG-20170531-WA0004.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304810507,\"cover\":false},{\"id\":19200,\"imgName\":\"IMG_1225.JPG\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":true,\"imageHeight\":157,\"imageWidth\":117,\"albumName\":\"2017-07-01 Family\",\"rating\":3,\"thumbPath\":\"thumbs/2017-07-01 Family/1498931913000/IMG_1225.JPG\",\"imagePath\":\"albums/2017-07-01 Family/1498931913000/IMG_1225.JPG\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304813236,\"cover\":true},{\"id\":19420,\"imgName\":\"IMG-20170530-WA0007.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676846000/IMG-20170530-WA0007.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676846000/IMG-20170530-WA0007.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304809362,\"cover\":false},{\"id\":19196,\"imgName\":\"IMG_1182.JPG\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1498931893000/IMG_1182.JPG\",\"imagePath\":\"albums/2017-07-01 Family/1498931893000/IMG_1182.JPG\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304812291,\"cover\":false},{\"id\":19197,\"imgName\":\"IMG_1204.JPG\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1498931898000/IMG_1204.JPG\",\"imagePath\":\"albums/2017-07-01 Family/1498931898000/IMG_1204.JPG\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304812781,\"cover\":false},{\"id\":19198,\"imgName\":\"IMG_1180.JPG\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1498931900000/IMG_1180.JPG\",\"imagePath\":\"albums/2017-07-01 Family/1498931900000/IMG_1180.JPG\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304812213,\"cover\":false},{\"id\":19199,\"imgName\":\"IMG_1202.JPG\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":2,\"thumbPath\":\"thumbs/2017-07-01 Family/1498931904000/IMG_1202.JPG\",\"imagePath\":\"albums/2017-07-01 Family/1498931904000/IMG_1202.JPG\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304812653,\"cover\":false},{\"id\":19201,\"imgName\":\"IMG_1224.JPG\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1498931918000/IMG_1224.JPG\",\"imagePath\":\"albums/2017-07-01 Family/1498931918000/IMG_1224.JPG\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304813152,\"cover\":false},{\"id\":19202,\"imgName\":\"IMG_1227.JPG\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":117,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1498931926000/IMG_1227.JPG\",\"imagePath\":\"albums/2017-07-01 Family/1498931926000/IMG_1227.JPG\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304813456,\"cover\":false},{\"id\":19203,\"imgName\":\"IMG_1201.JPG\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1498931931000/IMG_1201.JPG\",\"imagePath\":\"albums/2017-07-01 Family/1498931931000/IMG_1201.JPG\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304812565,\"cover\":false},{\"id\":19205,\"imgName\":\"IMG_1222.JPG\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1498931940000/IMG_1222.JPG\",\"imagePath\":\"albums/2017-07-01 Family/1498931940000/IMG_1222.JPG\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304812961,\"cover\":false},{\"id\":19206,\"imgName\":\"IMG_1200.JPG\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1498931945000/IMG_1200.JPG\",\"imagePath\":\"albums/2017-07-01 Family/1498931945000/IMG_1200.JPG\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304812487,\"cover\":false},{\"id\":19207,\"imgName\":\"IMG_1226.JPG\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":117,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1498931954000/IMG_1226.JPG\",\"imagePath\":\"albums/2017-07-01 Family/1498931954000/IMG_1226.JPG\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304813376,\"cover\":false},{\"id\":19208,\"imgName\":\"IMG_1205.JPG\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1498931959000/IMG_1205.JPG\",\"imagePath\":\"albums/2017-07-01 Family/1498931959000/IMG_1205.JPG\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304812875,\"cover\":false},{\"id\":19418,\"imgName\":\"IMG-20170530-WA0009.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":88,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676842000/IMG-20170530-WA0009.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676842000/IMG-20170530-WA0009.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304809545,\"cover\":false},{\"id\":19419,\"imgName\":\"IMG-20170530-WA0010.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":88,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676843000/IMG-20170530-WA0010.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676843000/IMG-20170530-WA0010.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304809640,\"cover\":false},{\"id\":19195,\"imgName\":\"IMG_1183.JPG\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1498931887000/IMG_1183.JPG\",\"imagePath\":\"albums/2017-07-01 Family/1498931887000/IMG_1183.JPG\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304812398,\"cover\":false},{\"id\":19421,\"imgName\":\"IMG-20170530-WA0014.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":94,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676847000/IMG-20170530-WA0014.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676847000/IMG-20170530-WA0014.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304810008,\"cover\":false},{\"id\":19422,\"imgName\":\"IMG-20170531-WA0009.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":117,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676850000/IMG-20170531-WA0009.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676850000/IMG-20170531-WA0009.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304810966,\"cover\":false},{\"id\":19423,\"imgName\":\"IMG-20170531-WA0007.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":117,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676851000/IMG-20170531-WA0007.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676851000/IMG-20170531-WA0007.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304810790,\"cover\":false},{\"id\":19424,\"imgName\":\"IMG-20170530-WA0006.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676852000/IMG-20170530-WA0006.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676852000/IMG-20170530-WA0006.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304809256,\"cover\":false},{\"id\":19425,\"imgName\":\"IMG-20170525-WA0016.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":94,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676853000/IMG-20170525-WA0016.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676853000/IMG-20170525-WA0016.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304808536,\"cover\":false},{\"id\":19426,\"imgName\":\"IMG-20170531-WA0020.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676854000/IMG-20170531-WA0020.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676854000/IMG-20170531-WA0020.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304811859,\"cover\":false},{\"id\":19427,\"imgName\":\"IMG-20170531-WA0010.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676855000/IMG-20170531-WA0010.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676855000/IMG-20170531-WA0010.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304811071,\"cover\":false},{\"id\":19428,\"imgName\":\"IMG-20170530-WA0011.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676857000/IMG-20170530-WA0011.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676857000/IMG-20170530-WA0011.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304809733,\"cover\":false},{\"id\":19429,\"imgName\":\"IMG-20170531-WA0005.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":117,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676858000/IMG-20170531-WA0005.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676858000/IMG-20170531-WA0005.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304810610,\"cover\":false},{\"id\":19430,\"imgName\":\"IMG-20170530-WA0004.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676859000/IMG-20170530-WA0004.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676859000/IMG-20170530-WA0004.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304809034,\"cover\":false},{\"id\":19431,\"imgName\":\"IMG-20170531-WA0002.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":157,\"imageWidth\":117,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676861000/IMG-20170531-WA0002.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676861000/IMG-20170531-WA0002.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304810331,\"cover\":false},{\"id\":19432,\"imgName\":\"IMG-20170531-WA0019.jpg\",\"hidden\":false,\"personal\":false,\"ugly\":false,\"duplicate\":false,\"printable\":false,\"imageHeight\":88,\"imageWidth\":157,\"albumName\":\"2017-07-01 Family\",\"rating\":1,\"thumbPath\":\"thumbs/2017-07-01 Family/1500676862000/IMG-20170531-WA0019.jpg\",\"imagePath\":\"albums/2017-07-01 Family/1500676862000/IMG-20170531-WA0019.jpg\",\"dateTime\":1518304797000,\"imageLastUpdate\":1518304811771,\"cover\":false}]";
        List<AlbumPage> albumPages = mapper.readValue(json, new TypeReference<List<AlbumPage>>() {});
        assertThat(albumPages, hasSize(StringUtils.countOccurrencesOf(json, "{")));
        logger.debug(albumPages.stream().map(AlbumPage::getImgName)
                .collect(Collectors.joining("\n")));
    }
}
