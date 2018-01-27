package image.exifweb.pyload;

import image.exifweb.appmanager.AppManagerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: adriana
 * Date: 12/1/14
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PyLoadService extends AppManagerService {
	@Value("${pyLoadCore.path}")
	String pyLoadCorePath;

	@PostConstruct
	public void postConstruct() {
		appProcName = "pyLoad";
		appStatus = new ProcessBuilder("pgrep", "-f", pyLoadCorePath);
		appStart = new ProcessBuilder("python", pyLoadCorePath, "--no-remote", "--daemon");
	}

	protected String getPID() throws IOException, InterruptedException {
		return processInfoService.pgrep(pyLoadCorePath, true);
	}
}