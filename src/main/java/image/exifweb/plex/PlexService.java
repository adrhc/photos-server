package image.exifweb.plex;

import image.exifweb.appmanager.AppManagerService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 8/2/14
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PlexService extends AppManagerService {
    @PostConstruct
    public void postConstruct() {
        appProcName = "Plex Media Server";
        appStart = new ProcessBuilder(
            "/ffp/start/plexmediaserver.sh", "start");
//        appStop = new ProcessBuilder("kill", "`pidof Plex\\ Media\\ Server`");
//        appStopForce = new ProcessBuilder("kill", "-9", "`pidof Plex\\ Media\\ Server`");
    }
}
