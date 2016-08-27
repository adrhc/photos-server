package image.exifweb.plex;

import image.exifweb.appmanager.AppManagerCtrl;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 8/2/14
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/json/plex")
public class PlexCtrl extends AppManagerCtrl {
    @Inject
    private ApplicationContext ac;

    @PostConstruct
    public void postConstruct() {
        this.appManagerService = ac.getBean(PlexService.class);
    }
}
