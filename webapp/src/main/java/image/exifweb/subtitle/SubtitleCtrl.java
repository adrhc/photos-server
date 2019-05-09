package image.exifweb.subtitle;

import image.exifweb.util.io.EndingLinesFileReader;
import image.exifweb.util.procinfo.ProcessInfoService;
import image.exifweb.web.json.JsonStringValue;
import image.photos.config.AppConfigService;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by adrian.petre on 18-06-2014.
 */
@RestController
@RequestMapping("/json/subtitle")
public class SubtitleCtrl {
	@Inject
	private ApplicationContext ac;
	@Inject
	private AppConfigService appConfigService;
	@Inject
	private SubtitleService subtitleService;
	@Inject
	private ProcessInfoService processInfoService;

	@PostMapping(value = "/checkSubtitlesExtractor", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void checkSubtitlesExtractor(Model model) throws Exception {
		String runningMessage;
		if (this.subtitleService.isSubtitlesExtractorRunning()) {
			runningMessage = "Subtitles extractor is running !";
		} else {
			List<String> runningCmds = this.processInfoService.getProcessesRunning(SubtitleService.SUBTITLE_EXTRACT_COMMANDS);
			if (runningCmds.isEmpty()) {
				runningMessage =
						StringUtils.arrayToCommaDelimitedString(SubtitleService.SUBTITLE_EXTRACT_COMMANDS) + " NOT running !";
			} else {
				runningMessage = "Commands: " +
						StringUtils.collectionToCommaDelimitedString(runningCmds) + " running !";
			}
		}
		EndingLinesFileReader endingLinesFileReader = this.ac.getBean(EndingLinesFileReader.class);
		endingLinesFileReader.setRunningMessage(runningMessage);
		model.addAttribute("subLogLines", endingLinesFileReader.getLines());
	}

	@PostMapping(value = "/extractSubtitles", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public synchronized void extractSubtitles(@RequestBody JsonStringValue jsonStringValue, Model model) throws IOException, InterruptedException {
		boolean mkvExtractStarted = this.subtitleService.extractSubtitles(jsonStringValue.getValue(), true);
		if (mkvExtractStarted) {
			if (StringUtils.hasText(jsonStringValue.getValue())) {
				model.addAttribute("message", "extractSubtitles started for " + jsonStringValue.getValue() + " !");
			} else {
				model.addAttribute("message", "extractSubtitles started for all videos !");
			}
		} else {
			model.addAttribute("message", "extractSubtitles already running for " +
					this.subtitleService.getMkvExtractorProcessFolder() + " !");
			model.addAttribute("error", Boolean.TRUE);
		}
	}

	@RequestMapping(value = "/stopExtractingSubtitles", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public synchronized void stopExtractingSubtitles(Model model) throws IOException, InterruptedException {
		boolean mkvExtractStopped = this.subtitleService.stopExtractingSubtitles();
		if (mkvExtractStopped) {
			model.addAttribute("message", "extractSubtitles stopped !");
		} else {
			model.addAttribute("message", "extractSubtitles NOT running ! (s-a dat totusi kill pe orice process %subtitles-extractor%)");
			model.addAttribute("error", Boolean.TRUE);
		}
	}

	@RequestMapping(value = "/videoFolders", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public List<JsonStringValue> videoFolders(WebRequest webRequest) throws IOException {
		File videoRoot = new File(this.appConfigService.getConfig("video root folder"));
		File[] videoRootFolders = videoRoot.listFiles(File::isDirectory);
		List<JsonStringValue> directDirKids = new ArrayList<>(videoRootFolders.length);
		long lastModified = 0;
		for (File path : videoRootFolders) {
			directDirKids.add(new JsonStringValue(path.getName()));
			if (lastModified < path.lastModified()) {
				lastModified = path.lastModified();
			}
		}
		if (webRequest.checkNotModified(lastModified)) {
			return null;
		}
		Collections.sort(directDirKids);
		return directDirKids;
	}
}
