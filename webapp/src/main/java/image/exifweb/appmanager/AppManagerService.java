package image.exifweb.appmanager;

import image.exifweb.util.procinfo.ProcessInfoService;
import image.photos.util.process.ProcessRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 8/2/14
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AppManagerService {
	private static final Logger logger = LoggerFactory.getLogger(ProcessInfoService.class);
	/**
	 * appProcName folosit de pidof si in loguri
	 */
	protected String appProcName;
	protected ProcessBuilder appStatus;
	protected ProcessBuilder appStart;
	protected ProcessBuilder appStop;
	protected ProcessBuilder appStopForce;
	@Autowired
	protected ProcessInfoService processInfoService;
	@Autowired
	private ProcessRunner processRunner;
	@Value("${wait.to.verify.kill}")
	private int waitToVerifyKill;

	public boolean isRunning() throws IOException, InterruptedException {
		if (this.appStatus != null) {
			return StringUtils.hasText(this.processRunner.getProcessOutput(this.appStatus));
		} else {
			return StringUtils.hasText(getPID());
		}
	}

	public void start() throws IOException {
		this.appStart.start();
	}

	public void stop() throws IOException, InterruptedException {
		try {
			if (this.appStop == null) {
				String pid = getPID();
				if (StringUtils.hasText(pid)) {
					new ProcessBuilder("kill", pid).start();
				}
			} else {
				this.appStop.start();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		safeWait();
		if (isRunning()) {
			try {
				if (this.appStopForce == null) {
					String pid = getPID();
					if (StringUtils.hasText(pid)) {
						logger.warn("Forced kill of {}", this.appProcName);
						new ProcessBuilder("kill", "-9", pid).start();
					}
				} else {
					logger.warn("Forced kill of {}", this.appProcName);
					this.appStopForce.start();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	protected String getPID() throws IOException, InterruptedException {
		String[] pids = this.processInfoService.pidof(this.appProcName);
		if (pids.length == 0) {
			return null;
		}
		return pids[0];
	}

	private void safeWait() {
		try {
			Thread.sleep(this.waitToVerifyKill);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public String getAppProcName() {
		return this.appProcName;
	}
}
