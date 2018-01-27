package image.exifweb.appmanager;

import image.exifweb.sys.ProcessInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 8/2/14
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppManagerService {
	private static final Logger logger = LoggerFactory.getLogger(ProcessInfoService.class);
	/**
	 * appProcName folosit de pidof si in loguri
	 */
	protected String appProcName;
	protected ProcessBuilder appStatus;
	protected ProcessBuilder appStart;
	protected ProcessBuilder appStop;
	protected ProcessBuilder appStopForce;
	@Inject
	protected ProcessInfoService processInfoService;
	@Value("${wait.to.verify.kill}")
	private int waitToVerifyKill;

	public boolean isRunning() throws IOException, InterruptedException {
		if (appStatus != null) {
			return StringUtils.hasText(processInfoService.getProcessOutput(appStatus));
		} else {
			return StringUtils.hasText(getPID());
		}
	}

	public void start() throws IOException {
		appStart.start();
	}

	public void stop() throws IOException, InterruptedException {
		try {
			if (appStop == null) {
				String pid = getPID();
				if (StringUtils.hasText(pid)) {
					new ProcessBuilder("kill", pid).start();
				}
			} else {
				appStop.start();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		safeWait();
		if (isRunning()) {
			try {
				if (appStopForce == null) {
					String pid = getPID();
					if (StringUtils.hasText(pid)) {
						logger.warn("Forced kill of {}", appProcName);
						new ProcessBuilder("kill", "-9", pid).start();
					}
				} else {
					logger.warn("Forced kill of {}", appProcName);
					appStopForce.start();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	protected String getPID() throws IOException, InterruptedException {
		String[] pids = processInfoService.pidof(appProcName);
		if (pids.length == 0) {
			return null;
		}
		return pids[0];
	}

	private void safeWait() {
		try {
			Thread.sleep(waitToVerifyKill);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public String getAppProcName() {
		return appProcName;
	}
}
