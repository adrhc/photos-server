package image.exifweb.transmission;

import image.exifweb.appmanager.AppManagerService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 8/2/14
 * Time: 12:38 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class TransmissionService extends AppManagerService {
	@PostConstruct
	public void postConstruct() {
		appProcName = "transmission-daemon";
		appStart = new ProcessBuilder(
				"/ffp/start/transmission.sh", "start");
	}
}
