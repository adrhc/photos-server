package image.exifweb.plex;

import image.exifweb.appmanager.AppManagerCtrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 8/2/14
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/json/plex")
public class PlexCtrl extends AppManagerCtrl {}
