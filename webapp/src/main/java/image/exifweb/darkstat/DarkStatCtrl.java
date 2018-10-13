package image.exifweb.darkstat;

import image.exifweb.appmanager.AppManagerCtrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 2/21/15
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/json/darkstat")
public class DarkStatCtrl extends AppManagerCtrl<DarkStatService> {}
