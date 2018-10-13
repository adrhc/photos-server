package image.exifweb.appconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import image.cdm.AppConfig;
import image.exifweb.util.MailService;
import image.exifweb.util.procinfo.ProcStatPercent;
import image.exifweb.util.procinfo.ProcessInfoService;
import image.persistence.repositories.AppConfigRepository;
import image.photos.config.AppConfigService;
import image.photos.util.conversion.PhotosConversionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RestController
@RequestMapping("/json/appconfig")
public class AppConfigCtrlImpl implements IAppConfigCache {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigCtrlImpl.class);
	@Inject
	private ProcessInfoService processInfoService;
	@Inject
	private MailService mailService;
	@Inject
	private AppConfigRepository appConfigRepository;
	@Inject
	private AppConfigService appConfigService;
	@Inject
	private ObjectMapper objectMapper;
	@Inject
	private ApplicationContext ac;
	@Inject
	private ConversionService cs;
	@Inject
	private PhotosConversionUtil photosConversionSupport;
	private String testRAMString = "eYAcmfLplzCjc8zBvuWXmcZ9MjyiQFwnr5ZTFmC9lhObiHR4txz00II8vFXgxpWtamROf3etqVjRvGvBreeAIe50hWjMqOURzx1V318hbOp4ixf4J8nlVVl4JfJrjqMLopTX2WiUmHajurtzfxiXbH367wY1DL43wCE78wR43LryHzEhHMscWBbHHI42pK0atakSx4XFTvoIWGsMJJn58p4HkDdvud8G0M5CxGPK4s0HQc6LDZwiVUW3BOOGuRFVPWfDj9mAiSiASxc30HfuWPOV5nkQfNDeFvWmOOd5FpGwPwVG2Ap3Xq3Yt8FSMUkd9rmWTuDV8fI1wxU6Sbo4srrzQDnpYgh4iLGv9QrG2r3Hn4qAb5EnuzRZfOMN1SuPA4MUKwdOBOfMRN5uy03EJUo631tyGT3RassGmv3Mk74EBpROhMKb93VYwYC28U26XPtATCJkq9qTuoemzXXF34ADfOVem0sal9g9NHrDonz7zbb08llPKErXqOd8gFsYbnSy7nTAAi7RJ3YYnVn2Hg1c9SNHvvy3IZZfoOFh7W1CNWuulccPQLWMYILpLxB0hekhB1x3B7TmTPcIDwKaK7manOH29MY58PIULmQZS5tfOhKyv2DpszhMtfAALYat6YV6VmvCHmafdUS2nvbmR51SIYBlH4JZSpLu83A9CxWVplqMGl1SkYMIDztIFM5FUo9iJnFokkAoFSTHctdUSOzoUzRUOttxaVS3KoTsROoG3eMN0VQLiwGuPXSKvKvObf8EhXXG1KoZw9bidjY32b2wSGa5vRajRHfKkxxAw5i3tQEf4jJjtgLKpjikLemmleQWVvcNI8QxfYmma3m7Q6lqIH071Zm8NXRNLzuhpfTBprb0JS971WApjMk6r9J7nA5qp1hjGhFbEPvoccVvvW0JzTCnpD1wNB7erHIB3gpDsGPbQR4cmd9T4ZwFrL1nMuI6Teaw8T496IYuJjMbShMLhOMq2htNVHDACYjO11xdpwNIFWjBIUMGaNgR2AEd";
	private List<AppConfig> testRAMObjectToJson;


	@RequestMapping("/subscribeToAsyncProcMemStats")
	public DeferredResult<Model> subscribeToAsyncProcMemStats() {
		return new CPUMemSummaryDeferredResult(this.processInfoService.asyncSubscribers);
	}


	@RequestMapping(value = "/getProcMemStatSummary",
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void getProcMemStatSummary(Model model)
			throws IOException, InterruptedException {
		this.processInfoService.prepareCPUMemSummary(model, null);
	}


	@RequestMapping(value = "/getProcMemFullStats", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> getProcMemFullStats() {
		// valid only on NSA310: processInfoService.prepareProcMemFullStats(model);
		// HttpHeaders responseHeaders = new HttpHeaders();
		// responseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		// return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
		return new ResponseEntity<>(HttpStatus.OK);
	}


	@RequestMapping(value = "/testRAMObjectToJson", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<AppConfig> testRAMObjectToJson() {
		return this.testRAMObjectToJson;
	}


	@RequestMapping(value = "/testRAMObjectToJsonDeferred", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public DeferredResult<List<AppConfig>> testRAMObjectToJsonDeferred() {
		return (new ConstantDeferredResult<List<AppConfig>>()).setResultThenRun(this.testRAMObjectToJson);
	}


	@RequestMapping(value = "/testRAMString", method = RequestMethod.GET,
			produces = MediaType.TEXT_PLAIN_VALUE)
	public String testRAMString() {
		return this.testRAMString;
	}


	@RequestMapping(value = "/testRAMStringDeferred", method = RequestMethod.GET,
			produces = MediaType.TEXT_PLAIN_VALUE)
	public DeferredResult<String> testRAMStringDeferred() {
		return this.ac.getBean(StringConstDeferredResult.class).setString(this.testRAMString);
	}


	@RequestMapping(value = "/getMemStat", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<ProcStatPercent> getMemStat() throws IOException, InterruptedException {
		return this.processInfoService.getMemDetailUsingPs();
	}


	@RequestMapping(value = "/getProcStat", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<ProcStatPercent> getProcStat() throws IOException, InterruptedException {
		return this.processInfoService.getCPUDetailUsingTop();
	}


	@RequestMapping(value = "/gc", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void gc(Model model) {
		System.gc();
		model.addAttribute("message", "System.gc run!");
	}


	@RequestMapping(value = "/checkProcess", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void checkProcess(@RequestParam String[] commands, Model model) throws Exception {
		List<String> runningCmds = this.processInfoService.getProcessesRunning(commands);
		if (runningCmds.isEmpty()) {
			model.addAttribute("message",
					StringUtils.arrayToCommaDelimitedString(commands) + " not running!");
			model.addAttribute("error", Boolean.TRUE);
		} else {
			model.addAttribute("message",
					StringUtils.collectionToCommaDelimitedString(runningCmds) + " running!");
		}
	}


	@RequestMapping(value = "/checkMailService", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void checkMailService(Model model) throws Exception {
		if (this.mailService.checkMailService()) {
			model.addAttribute("message", "Mail service is running!");
		} else {
			model.addAttribute("message", "Mail service is NOT running!");
			model.addAttribute("error", Boolean.TRUE);
		}
	}


	@RequestMapping(value = "/reloadParams", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void reloadParams(Model model) {
		evictAppConfigCache();
		model.addAttribute("message", "App params reloaded!");
	}


	@RequestMapping(value = "/updateAppConfigs", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void update(@RequestBody List<AppConfig> appConfigs, Model model) throws IOException {
		this.appConfigRepository.saveAll(this.photosConversionSupport.entityAppConfigsOf(appConfigs));
		this.appConfigService.writeJsonForAppConfigs();
		model.addAttribute("message", "App configs updated!");
	}


	@RequestMapping(value = "/canUseJsonFiles", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Map<String, String> canUseJsonFiles(WebRequest webRequest) {
		if (webRequest.checkNotModified(this.appConfigService.canUseJsonFilesLastUpdate())) {
			return null;
		}
		Map<String, String> map = new HashMap<>();
		map.put("use json files", this.appConfigService.getConfig("use json files"));
		map.put("use json files for config",
				this.appConfigService.getConfig("use json files for config"));
		return map;
	}


	@RequestMapping(value = "getAppConfigs", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<AppConfig> getAppConfigs(WebRequest webRequest) {
//        logger.debug("lastUpdatedAppConfigs = {}", appConfigService.getLastUpdatedAppConfigs());
//        logger.debug("If-Modified-Since = {}", request.getDateHeader("If-Modified-Since"));
//        logger.debug("currentTimeMillis = {}", System.currentTimeMillis());
//        logger.debug("getDBNow = {}", appConfigService.getDBNow().getTime());
		if (webRequest.checkNotModified(
				this.appConfigService.getLastUpdatedAppConfigs())) {
//            logger.debug("not modified");
			return null;
		}
//        List<AppConfig> appConfigs = appConfigService.getAppConfigs();
//        logger.debug("modified:\n{}", ArrayUtils.toString(appConfigs));
//        return appConfigs;
		return this.photosConversionSupport.cdmAppConfigsOf(
				this.appConfigRepository.findAll());
	}


	@RequestMapping(value = "findAllOrderByNameAscNotCached",
			method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<AppConfig> findAllOrderByNameAscNotCached() {
		return this.photosConversionSupport.cdmAppConfigsOf(
				this.appConfigRepository.findAllOrderByNameAscNotCached());
	}


	@RequestMapping(value = "findByNameNotCached",
			method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public AppConfig findByNameNotCached() {
		return this.cs.convert(
				this.appConfigRepository.findByNameNotCached("albums_path"),
				AppConfig.class);
	}


	@PostConstruct
	public void postConstruct() {
		this.testRAMObjectToJson = this.photosConversionSupport.cdmAppConfigsOf(
				this.appConfigRepository.findAll());
		if (this.testRAMString != null) {
			logger.debug("Using not null testRAMString with length {}", this.testRAMString.length());
			return;
		}
		try {
			this.testRAMString = this.objectMapper.writeValueAsString(this.testRAMObjectToJson);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
