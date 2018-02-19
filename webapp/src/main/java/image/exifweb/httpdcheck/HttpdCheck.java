package image.exifweb.httpdcheck;

import image.exifweb.util.procinfo.ProcessInfoService;
import image.photos.config.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 12/10/13
 * Time: 8:55 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HttpdCheck {
	public static final String TRY_RESTART_MSG = "Try to restart httpd !";
	public static final String TRY_RESTART_OK_MSG = "Httpd restarted !";
	private static final Logger logger = LoggerFactory.getLogger(HttpdCheck.class);
	@Inject
	private ProcessInfoService processInfoService;
	@Inject
	private AppConfigService appConfigService;
	private ProcessBuilder xhttpd = new ProcessBuilder("/ffp/start/httpd.sh", "start");
	private ProcessBuilder adminHttpd = new ProcessBuilder("/ffp/start/httpd-admin.sh", "start");
	@Value("${httpd.pid}")
	private String xhttpdPid;
	@Value("${httpd-admin.pid}")
	private String httpdAdminPid;
//	@Value("${httpd-admin.start.wait}")
//	private long httpdAdminStartWait;

	public void checkHttpd() throws IOException, InterruptedException {
		if (appConfigService.getConfigBool("stop_httpd_checking")) {
			return;
		}
		if (processInfoService.getPidByFile(httpdAdminPid) == null) {
			restartHttpd(true);
//			Thread.sleep(httpdAdminStartWait);
			restartHttpd(false);
		} else if (processInfoService.getPidByFile(xhttpdPid) == null) {
			restartHttpd(false);
		}
	}

	private void restartHttpd(boolean useAdminHttpd) {
		logger.warn(TRY_RESTART_MSG);
		System.gc();
		try {
			Process p;
			if (useAdminHttpd) {
				p = adminHttpd.start();
			} else {
				p = xhttpd.start();
			}
			p.waitFor();
			logger.warn(TRY_RESTART_OK_MSG);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public String getHttpdRestartLogs() throws IOException {
		StringBuilder sb = new StringBuilder();
		String httpd_restart_logs = appConfigService.getConfig("httpd_restart_logs");
		readHttpdRestartLogFile(httpd_restart_logs + "/exifweb.log.3", sb);
		readHttpdRestartLogFile(httpd_restart_logs + "/exifweb.log.2", sb);
		readHttpdRestartLogFile(httpd_restart_logs + "/exifweb.log.1", sb);
		readHttpdRestartLogFile(httpd_restart_logs + "/exifweb.log", sb);
		return sb.toString();
	}

	private void readHttpdRestartLogFile(String logFilePath, StringBuilder sb) throws IOException {
		if (!new File(logFilePath).exists()) {
			return;
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(logFilePath)));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(TRY_RESTART_MSG) || line.contains(TRY_RESTART_OK_MSG)) {
					sb.append(line).append('\n');
				}
			}
		} finally {
			if (reader != null) reader.close();
		}
	}
}
