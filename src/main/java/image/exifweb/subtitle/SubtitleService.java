package image.exifweb.subtitle;

import image.exifweb.sys.AppConfigService;
import image.exifweb.sys.ProcessInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import subtitles.config.RuntimeStatus;
import subtitles.movie.MovieFolder;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * Created by adrian.petre on 18-06-2014.
 */
@Service
public class SubtitleService {
	public static final String[] SUBTITLE_EXTRACT_COMMANDS = {
			"subtitles-extractor", "mkvinfo", "mkvmerge", "mkvextract"};
	private static final Logger logger = LoggerFactory.getLogger(SubtitleService.class);
	@Inject
	private ProcessInfoService processInfoService;
	@Inject
	private AppConfigService appConfigService;
	@Inject
	private ApplicationContext ac;
	private ProcessBuilder mkvExtractor =
			new ProcessBuilder("/home/adr/subtitles-extractor-1.0-SNAPSHOT.sh");
	private Process mkvExtractorProcess = null;
	private String mkvExtractorProcessFolder = null;
	private RuntimeStatus runtimeStatus = null;

	public synchronized boolean isSubtitlesExtractorRunning() {
		return mkvExtractorProcess != null || runtimeStatus != null;
	}

	public synchronized boolean stopExtractingSubtitles() throws IOException, InterruptedException {
		if (runtimeStatus != null) {
			runtimeStatus.askToStop();
			runtimeStatus = null;// marchez finalizarea extragerii
			return true;
		} else if (mkvExtractorProcess != null) {
			mkvExtractorProcess.destroy();// de fapt niciodata nu a functionat
			mkvExtractorProcess = null;// marchez finalizarea extragerii
			wait(250);// asteptam ca proc sa fie distrus
			processInfoService.killSubtitlesExtractor();
			mkvExtractorProcessFolder = null;
			return true;
		} else {
			processInfoService.killSubtitlesExtractor();
			return false;
		}
	}

	public boolean extractSubtitles(String startVideoDir, boolean appendVideoRoot) throws IOException, InterruptedException {
		if (isSubtitlesExtractorRunning()) {
			return false;
		}
		doExtractSubtitles(startVideoDir, appendVideoRoot);
		return true;
	}

	@Async
	private void doExtractSubtitles(String startVideoDir, boolean appendVideoRoot) {
		if (!StringUtils.hasText(startVideoDir)) {
			startVideoDir = appConfigService.getConfig("video root folder");
		} else if (appendVideoRoot) {
			startVideoDir = appConfigService.getConfig("video root folder") +
					File.separatorChar + startVideoDir;
		}
		runtimeStatus = ac.getBean(RuntimeStatus.class);
		runtimeStatus.getRuntimeOptions().init(startVideoDir);
		logger.debug("runtimeOptions: " + runtimeStatus.getRuntimeOptions());
		MovieFolder movieFolder = ac.getBean(MovieFolder.class);
		movieFolder.init(runtimeStatus);
		StopWatch sw = new StopWatch();
		sw.start();
		movieFolder.process();
		sw.stop();
		runtimeStatus = null;// marchez finalizarea extragerii
		logger.debug(sw.shortSummary());
		logger.debug(ac.getBean(RuntimeStatus.class).toString());
	}

	@Async
	private void startExtractSubtitles(String startVideoDir) {
		ProcessBuilder mkvExtractor1;
		if (startVideoDir == null) {
			mkvExtractor1 = mkvExtractor;
		} else {
			mkvExtractor1 = new ProcessBuilder(mkvExtractor.command().get(0), "-dir=" + startVideoDir);
		}
		try {
			mkvExtractorProcess = mkvExtractor1.start();
			mkvExtractorProcessFolder = startVideoDir;
			int ret = mkvExtractorProcess.waitFor();
			if (startVideoDir != null) {
				logger.debug("extractSubtitles done for {} with: {}", startVideoDir, ret);
			} else {
				logger.debug("extractSubtitles done with: {}", ret);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (startVideoDir != null) {
				logger.error("For folder: {}", startVideoDir);
			}
		}
		mkvExtractorProcess = null;// marchez finalizarea extragerii
		mkvExtractorProcessFolder = null;
	}

	public String getMkvExtractorProcessFolder() {
		if (runtimeStatus != null) {
			return runtimeStatus.getRuntimeOptions().getStartPath();
		} else {
			return mkvExtractorProcessFolder;
		}
	}
}
