package image.exifweb.darkstat;

import image.exifweb.appmanager.AppManagerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 2/21/15
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class DarkStatService extends AppManagerService {
	@Value("${darkstat.port}")
	private String darkstatPort;
	@Value("${darkstat.niceness}")
	private int darkstatNiceness;
	@Value("${system.default.niceness}")
	private int systemDefaultNiceness;

	@PostConstruct
	public void postConstruct() {
		appProcName = "darkstat";
		appStart = new ProcessBuilder("nice",
				'-' + String.valueOf(darkstatNiceness - systemDefaultNiceness),
				"darkstat", "-i", "egiga0", "-p", darkstatPort, "--base", "darkstat",
				"--no-dns", "-l", "192.168.1.31/255.255.255.0");
	}
}
