package image.exifweb.subtitle;

import image.exifweb.util.procinfo.ProcessInfoService;
import image.photos.config.AppConfigService;
import image.photos.util.conversion.StringToProcessBuilderConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import subtitles.config.RuntimeStatus;
import subtitles.movie.MovieFolder;

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
	private final ProcessInfoService processInfoService;
	private final AppConfigService appConfigService;
	private final ApplicationContext ac;
	private ProcessBuilder mkvExtractor;
	private Process mkvExtractorProcess = null;
	private String mkvExtractorProcessFolder = null;
	private RuntimeStatus runtimeStatus = null;

	public SubtitleService(ProcessInfoService processInfoService, AppConfigService appConfigService, ApplicationContext ac, @Value("${mkvExtractor}") String mkvExtractor) {
		this.processInfoService = processInfoService;
		this.appConfigService = appConfigService;
		this.ac = ac;
		this.mkvExtractor = new StringToProcessBuilderConverter().convert(mkvExtractor);
	}

	public synchronized boolean isSubtitlesExtractorRunning() {
		return this.mkvExtractorProcess != null || this.runtimeStatus != null;
	}

	public synchronized boolean stopExtractingSubtitles() throws IOException, InterruptedException {
		if (this.runtimeStatus != null) {
			this.runtimeStatus.askToStop();
			this.runtimeStatus = null;// marchez finalizarea extragerii
			return true;
		} else if (this.mkvExtractorProcess != null) {
			this.mkvExtractorProcess.destroy();// de fapt niciodata nu a functionat
			this.mkvExtractorProcess = null;// marchez finalizarea extragerii
			this.wait(250);// asteptam ca proc sa fie distrus
			this.processInfoService.killSubtitlesExtractor();
			this.mkvExtractorProcessFolder = null;
			return true;
		} else {
			this.processInfoService.killSubtitlesExtractor();
			return false;
		}
	}

	public boolean extractSubtitles(String startVideoDir, boolean appendVideoRoot) {
		if (this.isSubtitlesExtractorRunning()) {
			return false;
		}
		this.doExtractSubtitles(startVideoDir, appendVideoRoot);
		return true;
	}

	private void doExtractSubtitles(String startVideoDir, boolean appendVideoRoot) {
		if (!StringUtils.hasText(startVideoDir)) {
			startVideoDir = this.appConfigService.getConfig("video root folder");
		} else if (appendVideoRoot) {
			startVideoDir = this.appConfigService.getConfig("video root folder") +
					File.separatorChar + startVideoDir;
		}
		this.runtimeStatus = this.ac.getBean(RuntimeStatus.class);
		this.runtimeStatus.getRuntimeOptions().init(startVideoDir);
		logger.debug("runtimeOptions: " + this.runtimeStatus.getRuntimeOptions());
		MovieFolder movieFolder = this.ac.getBean(MovieFolder.class);
		movieFolder.init(this.runtimeStatus);
		StopWatch sw = new StopWatch();
		sw.start();
		movieFolder.process();
		sw.stop();
		this.runtimeStatus = null;// marchez finalizarea extragerii
		logger.debug(sw.shortSummary());
		logger.debug(this.ac.getBean(RuntimeStatus.class).toString());
	}

	private void startExtractSubtitles(String startVideoDir) {
		ProcessBuilder mkvExtractor1;
		if (startVideoDir == null) {
			mkvExtractor1 = this.mkvExtractor;
		} else {
			mkvExtractor1 = new ProcessBuilder(this.mkvExtractor.command().get(0), "-dir=" + startVideoDir);
		}
		try {
			this.mkvExtractorProcess = mkvExtractor1.start();
			this.mkvExtractorProcessFolder = startVideoDir;
			int ret = this.mkvExtractorProcess.waitFor();
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
		this.mkvExtractorProcess = null;// marchez finalizarea extragerii
		this.mkvExtractorProcessFolder = null;
	}

	public String getMkvExtractorProcessFolder() {
		if (this.runtimeStatus != null) {
			return this.runtimeStatus.getRuntimeOptions().getStartPath();
		} else {
			return this.mkvExtractorProcessFolder;
		}
	}
}
