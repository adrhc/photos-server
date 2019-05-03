package image.exifweb.pyload;

import image.exifweb.appmanager.AppManagerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Adrian
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
		this.appProcName = "pyLoad";
		this.appStatus = new ProcessBuilder("pgrep", "-f", this.pyLoadCorePath);
		this.appStart = new ProcessBuilder("python", this.pyLoadCorePath, "--no-remote", "--daemon");
	}

	@Override
	protected String getPID() throws IOException, InterruptedException {
		return this.processInfoService.pgrep(this.pyLoadCorePath, true);
	}
}
