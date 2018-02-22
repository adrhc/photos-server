package image.exifweb.appconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import image.exifweb.util.MailService;
import image.exifweb.util.procinfo.ProcStatPercent;
import image.exifweb.util.procinfo.ProcessInfoService;
import image.persistence.entity.AppConfig;
import image.persistence.integration.repository.AppConfigRepositoryImpl;
import image.photos.config.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 12/11/13
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/json/appconfig")
public class AppConfigCtrl implements IAppConfigCache {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigCtrl.class);
	@Inject
	private ProcessInfoService processInfoService;
	@Inject
	private MailService mailService;
	@Inject
	private AppConfigRepositoryImpl appConfigRepository;
	@Inject
	private AppConfigService appConfigService;
	@Inject
	private ObjectMapper objectMapper;
	@Inject
	private ApplicationContext ac;
	private String testRAMString;
	private List<AppConfig> testRAMObjectToJson;

	@RequestMapping("/subscribeToAsyncProcMemStats")
	@ResponseBody
	public DeferredResult<Model> subscribeToAsyncProcMemStats() {
		return new CPUMemSummaryDeferredResult(processInfoService.asyncSubscribers);
	}

	@RequestMapping(value = "/getProcMemStatSummary", produces = MediaType.APPLICATION_JSON_VALUE)
	public void getProcMemStatSummary(Model model)
			throws IOException, InterruptedException {
		processInfoService.prepareCPUMemSummary(model, null);
	}

	@RequestMapping(value = "/getProcMemFullStats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getProcMemFullStats()
			throws IOException, InterruptedException {
		// valid only on NSA310: processInfoService.prepareProcMemFullStats(model);
		// HttpHeaders responseHeaders = new HttpHeaders();
		// responseHeaders.setContentType(MediaType.APPLICATION_JSON_VALUE);
		// return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/testRAMObjectToJson", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<AppConfig> testRAMObjectToJson()
			throws IOException, InterruptedException {
		return testRAMObjectToJson;
	}

	@RequestMapping(value = "/testRAMObjectToJsonDeferred", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public DeferredResult<List<AppConfig>> testRAMObjectToJsonDeferred()
			throws IOException, InterruptedException {
		return (new ConstantDeferredResult<List<AppConfig>>()).setResultThenRun(testRAMObjectToJson);
	}

	@RequestMapping(value = "/testRAMString", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String testRAMString()
			throws IOException, InterruptedException {
		return testRAMString;
	}

	@RequestMapping(value = "/testRAMStringDeferred", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public DeferredResult<String> testRAMStringDeferred()
			throws IOException, InterruptedException {
		return ac.getBean(StringConstDeferredResult.class).setString(testRAMString);
	}

	@RequestMapping(value = "/getMemStat", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<ProcStatPercent> getMemStat() throws IOException, InterruptedException {
		return processInfoService.getMemDetailUsingPs();
	}

	@RequestMapping(value = "/getProcStat", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<ProcStatPercent> getProcStat() throws IOException, InterruptedException {
		return processInfoService.getCPUDetailUsingTop();
	}

	@RequestMapping(value = "/gc", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void gc(Model model) {
		System.gc();
		model.addAttribute("message", "System.gc run!");
	}

	@RequestMapping(value = "/checkProcess", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public void checkProcess(@RequestParam String[] commands, Model model) throws Exception {
		List<String> runningCmds = processInfoService.getProcessesRunning(commands);
		if (runningCmds.isEmpty()) {
			model.addAttribute("message",
					StringUtils.arrayToCommaDelimitedString(commands) + " not running!");
			model.addAttribute("error", Boolean.TRUE);
		} else {
			model.addAttribute("message",
					StringUtils.collectionToCommaDelimitedString(runningCmds) + " running!");
		}
	}

	@RequestMapping(value = "/checkMailService", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void checkMailService(Model model) throws Exception {
		if (mailService.checkMailService()) {
			model.addAttribute("message", "Mail service is running!");
		} else {
			model.addAttribute("message", "Mail service is NOT running!");
			model.addAttribute("error", Boolean.TRUE);
		}
	}

	@RequestMapping(value = "/reloadParams", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void reloadParams(Model model) {
		evictAppConfigCache();
		model.addAttribute("message", "App params reloaded!");
	}

	@RequestMapping(value = "/updateAppConfigs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void update(@RequestBody List<AppConfig> appConfigs, Model model) throws IOException {
		appConfigRepository.update(appConfigs);
		appConfigService.writeJsonForAppConfigs();
		model.addAttribute("message", "App configs updated!");
	}

	@RequestMapping(value = "/canUseJsonFiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, String> canUseJsonFiles(WebRequest webRequest) {
		if (webRequest.checkNotModified(appConfigService.canUseJsonFilesLastUpdate())) {
			return null;
		}
		Map<String, String> map = new HashMap<>();
		map.put("use json files", appConfigService.getConfig("use json files"));
		map.put("use json files for config",
				appConfigService.getConfig("use json files for config"));
		return map;
	}

	@RequestMapping(value = "getAppConfigs",
			method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<AppConfig> getAppConfigs(WebRequest webRequest) {
//        logger.debug("lastUpdatedAppConfigs = {}", appConfigService.getLastUpdatedAppConfigs());
//        logger.debug("If-Modified-Since = {}", request.getDateHeader("If-Modified-Since"));
//        logger.debug("currentTimeMillis = {}", System.currentTimeMillis());
//        logger.debug("getDBNow = {}", appConfigService.getDBNow().getTime());
		if (webRequest.checkNotModified(appConfigService.getLastUpdatedAppConfigs())) {
//            logger.debug("not modified");
			return null;
		}
//        List<AppConfig> appConfigs = appConfigService.getAppConfigs();
//        logger.debug("modified:\n{}", ArrayUtils.toString(appConfigs));
//        return appConfigs;
		return appConfigRepository.getAppConfigs();
	}

	@RequestMapping(value = "testGetNoCacheableOrderedAppConfigs",
			method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<AppConfig> testGetNoCacheableOrderedAppConfigs() {
		return appConfigRepository.testGetNoCacheableOrderedAppConfigs();
	}

	@RequestMapping(value = "testGetNoCacheableAppConfigByName",
			method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public AppConfig testGetNoCacheableAppConfigByName() {
		return appConfigRepository.testGetNoCacheableAppConfigByName("albums_path");
	}

	@PostConstruct
	public void postConstruct() {
		testRAMObjectToJson = appConfigRepository.getAppConfigs();
		try {
			testRAMString = objectMapper.writeValueAsString(testRAMObjectToJson);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
