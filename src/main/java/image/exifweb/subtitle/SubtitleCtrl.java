package image.exifweb.subtitle;

import image.exifweb.sys.AppConfigService;
import image.exifweb.sys.ProcessInfoService;
import image.exifweb.util.io.EndingLinesFileReader;
import image.exifweb.util.json.JsonValue;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by adrian.petre on 18-06-2014.
 */
@Controller
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

	@RequestMapping(value = "/checkSubtitlesExtractor", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void checkSubtitlesExtractor(Model model) throws Exception {
		String runningMessage;
		if (subtitleService.isSubtitlesExtractorRunning()) {
			runningMessage = "Subtitles extractor is running !";
		} else {
			List<String> runningCmds = processInfoService.getProcessesRunning(SubtitleService.SUBTITLE_EXTRACT_COMMANDS);
			if (runningCmds.isEmpty()) {
				runningMessage =
						StringUtils.arrayToCommaDelimitedString(SubtitleService.SUBTITLE_EXTRACT_COMMANDS) + " NOT running !";
			} else {
				runningMessage = "Commands: " +
						StringUtils.collectionToCommaDelimitedString(runningCmds) + " running !";
			}
		}
		EndingLinesFileReader endingLinesFileReader = ac.getBean(EndingLinesFileReader.class);
		endingLinesFileReader.setRunningMessage(runningMessage);
		model.addAttribute("subLogLines", endingLinesFileReader.getLines());
	}

	@RequestMapping(value = "/extractSubtitles", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public synchronized void extractSubtitles(@RequestBody JsonValue jsonValue, Model model) throws IOException, InterruptedException {
		boolean mkvExtractStarted = subtitleService.extractSubtitles(jsonValue.getValue(), true);
		if (mkvExtractStarted) {
			if (StringUtils.hasText(jsonValue.getValue())) {
				model.addAttribute("message", "extractSubtitles started for " + jsonValue.getValue() + " !");
			} else {
				model.addAttribute("message", "extractSubtitles started for all videos !");
			}
		} else {
			model.addAttribute("message", "extractSubtitles already running for " +
					subtitleService.getMkvExtractorProcessFolder() + " !");
			model.addAttribute("error", Boolean.TRUE);
		}
	}

	@RequestMapping(value = "/stopExtractingSubtitles", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public synchronized void stopExtractingSubtitles(Model model) throws IOException, InterruptedException {
		boolean mkvExtractStopped = subtitleService.stopExtractingSubtitles();
		if (mkvExtractStopped) {
			model.addAttribute("message", "extractSubtitles stopped !");
		} else {
			model.addAttribute("message", "extractSubtitles NOT running ! (s-a dat totusi kill pe orice process %subtitles-extractor%)");
			model.addAttribute("error", Boolean.TRUE);
		}
	}

	@RequestMapping(value = "/videoFolders", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public List<JsonValue> videoFolders(WebRequest webRequest) throws IOException {
		File videoRoot = new File(appConfigService.getConfig("video root folder"));
		File[] videoRootFolders = videoRoot.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		List<JsonValue> directDirKids = new ArrayList<>(videoRootFolders.length);
		long lastModified = 0;
		for (File path : videoRootFolders) {
			directDirKids.add(new JsonValue(path.getName()));
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
