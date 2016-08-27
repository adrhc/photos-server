package image.exifweb.stunnel;

import image.exifweb.appmanager.AppManagerService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/2/14
 * Time: 8:33 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class StunnelService extends AppManagerService {
    @PostConstruct
    public void postConstruct() {
        appProcName = "stunnel";
        appStart = new ProcessBuilder(
            "stunnel", "/ffp/etc/stunnel/stunnel.conf", "1>/ffp/var/stunnel.log", "2>/ffp/var/stunnel.log", "&");
        appStop = new ProcessBuilder("killall", "stunnel");
        appStopForce = new ProcessBuilder("killall", "-9", "stunnel");
    }
}
