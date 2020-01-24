package image.exifweb.appconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import image.cdm.AppConfig;
import image.exifweb.util.MailService;
import image.exifweb.util.procinfo.ProcStatPercent;
import image.exifweb.util.procinfo.ProcessInfoService;
import image.jpa2x.repositories.appconfig.AppConfigRepository;
import image.photos.config.AppConfigConversionHelper;
import image.photos.config.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 12/11/13
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/json/appconfig")
public class AppConfigCtrlImpl {
	private static final Logger logger = LoggerFactory.getLogger(AppConfigCtrlImpl.class);
	private final ProcessInfoService processInfoService;
	private final MailService mailService;
	private final AppConfigRepository appConfigRepository;
	private final AppConfigService appConfigService;
	private final ObjectMapper objectMapper;
	private final ApplicationContext ac;
	private final ConversionService conversionService;
	private final AppConfigConversionHelper appConfigConversionHelper;
	private String testRAMString = "eYAcmfLplzCjc8zBvuWXmcZ9MjyiQFwnr5ZTFmC9lhObiHR4txz00II8vFXgxpWtamROf3etqVjRvGvBreeAIe50hWjMqOURzx1V318hbOp4ixf4J8nlVVl4JfJrjqMLopTX2WiUmHajurtzfxiXbH367wY1DL43wCE78wR43LryHzEhHMscWBbHHI42pK0atakSx4XFTvoIWGsMJJn58p4HkDdvud8G0M5CxGPK4s0HQc6LDZwiVUW3BOOGuRFVPWfDj9mAiSiASxc30HfuWPOV5nkQfNDeFvWmOOd5FpGwPwVG2Ap3Xq3Yt8FSMUkd9rmWTuDV8fI1wxU6Sbo4srrzQDnpYgh4iLGv9QrG2r3Hn4qAb5EnuzRZfOMN1SuPA4MUKwdOBOfMRN5uy03EJUo631tyGT3RassGmv3Mk74EBpROhMKb93VYwYC28U26XPtATCJkq9qTuoemzXXF34ADfOVem0sal9g9NHrDonz7zbb08llPKErXqOd8gFsYbnSy7nTAAi7RJ3YYnVn2Hg1c9SNHvvy3IZZfoOFh7W1CNWuulccPQLWMYILpLxB0hekhB1x3B7TmTPcIDwKaK7manOH29MY58PIULmQZS5tfOhKyv2DpszhMtfAALYat6YV6VmvCHmafdUS2nvbmR51SIYBlH4JZSpLu83A9CxWVplqMGl1SkYMIDztIFM5FUo9iJnFokkAoFSTHctdUSOzoUzRUOttxaVS3KoTsROoG3eMN0VQLiwGuPXSKvKvObf8EhXXG1KoZw9bidjY32b2wSGa5vRajRHfKkxxAw5i3tQEf4jJjtgLKpjikLemmleQWVvcNI8QxfYmma3m7Q6lqIH071Zm8NXRNLzuhpfTBprb0JS971WApjMk6r9J7nA5qp1hjGhFbEPvoccVvvW0JzTCnpD1wNB7erHIB3gpDsGPbQR4cmd9T4ZwFrL1nMuI6Teaw8T496IYuJjMbShMLhOMq2htNVHDACYjO11xdpwNIFWjBIUMGaNgR2AEd";
	private List<AppConfig> testRAMObjectToJson;

	public AppConfigCtrlImpl(ProcessInfoService processInfoService, MailService mailService, AppConfigRepository appConfigRepository, AppConfigService appConfigService, ObjectMapper objectMapper, ApplicationContext ac, ConversionService conversionService, AppConfigConversionHelper appConfigConversionHelper) {
		this.processInfoService = processInfoService;
		this.mailService = mailService;
		this.appConfigRepository = appConfigRepository;
		this.appConfigService = appConfigService;
		this.objectMapper = objectMapper;
		this.ac = ac;
		this.conversionService = conversionService;
		this.appConfigConversionHelper = appConfigConversionHelper;
	}

	@RequestMapping("subscribeToAsyncProcMemStats")
	public DeferredResult<Model> subscribeToAsyncProcMemStats() {
		return new CPUMemSummaryDeferredResult(this.processInfoService.asyncSubscribers);
	}

	@RequestMapping(value = "getProcMemStatSummary",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public void getProcMemStatSummary(Model model)
			throws IOException, InterruptedException {
		this.processInfoService.prepareCPUMemSummary(model, null);
	}

	@GetMapping(value = "getProcMemFullStats",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getProcMemFullStats() {
		// valid only on NSA310: processInfoService.prepareProcMemFullStats(model);
		// HttpHeaders responseHeaders = new HttpHeaders();
		// responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		// return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
		return new ResponseEntity<>(OK);
	}

	@GetMapping(value = "testRAMObjectToJson",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public List<AppConfig> testRAMObjectToJson() {
		return this.testRAMObjectToJson;
	}

	@GetMapping(value = "testRAMObjectToJsonDeferred",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public DeferredResult<List<AppConfig>> testRAMObjectToJsonDeferred() {
		return (new ConstantDeferredResult<List<AppConfig>>())
				.setResultThenRun(this.testRAMObjectToJson);
	}

	@GetMapping(value = "testRAMString", produces = MediaType.TEXT_PLAIN_VALUE)
	public String testRAMString() {
		return this.testRAMString;
	}

	@GetMapping(value = "testRAMStringDeferred", produces = MediaType.TEXT_PLAIN_VALUE)
	public DeferredResult<String> testRAMStringDeferred() {
		return this.ac.getBean(StringConstDeferredResult.class).setString(this.testRAMString);
	}

	@GetMapping(value = "getMemStat", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ProcStatPercent> getMemStat() throws IOException, InterruptedException {
		return this.processInfoService.getMemDetailUsingPs();
	}

	@GetMapping(value = "getProcStat", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ProcStatPercent> getProcStat() throws IOException, InterruptedException {
		return this.processInfoService.getCPUDetailUsingTop();
	}

	@PostMapping(value = "gc", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public void gc(Model model) {
		System.gc();
		model.addAttribute("message", "System.gc run!");
	}

	@GetMapping(value = "checkProcess", produces = MediaType.APPLICATION_JSON_VALUE)
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

	@GetMapping(value = "checkMailService", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public void checkMailService(Model model) throws Exception {
		if (this.mailService.checkMailService()) {
			model.addAttribute("message", "Mail service is running!");
		} else {
			model.addAttribute("message", "Mail service is NOT running!");
			model.addAttribute("error", Boolean.TRUE);
		}
	}

	@PostMapping(value = "reloadParams", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public void reloadParams(Model model) {
		this.appConfigService.evictAppConfigCache();
		model.addAttribute("message", "App params reloaded!");
	}

	@PostMapping(value = "updateAppConfigs", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public void update(@RequestBody List<AppConfig> cdmAppConfigs, Model model) throws IOException {
		this.appConfigService.updateFromCdm(cdmAppConfigs);
		model.addAttribute("message", "App configs updated!");
	}

	@GetMapping(value = "canUseJsonFiles", produces = MediaType.APPLICATION_JSON_VALUE)
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

	@GetMapping(value = "getAppConfigs", produces = MediaType.APPLICATION_JSON_VALUE)
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
		return this.appConfigConversionHelper.cdmAppConfigsOf(
				this.appConfigRepository.findAll());
	}

	@GetMapping(value = "findAllOrderByNameAscNotCached",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public List<AppConfig> findAllOrderByNameAscNotCached() {
		return this.appConfigConversionHelper.cdmAppConfigsOf(
				this.appConfigRepository.findAllOrderByNameAscNotCached());
	}

	@GetMapping(value = "findByNameNotCached",
			produces = MediaType.APPLICATION_JSON_VALUE)
	public AppConfig findByNameNotCached() {
		return this.conversionService.convert(
				this.appConfigRepository.findByNameNotCached("albums_path"),
				AppConfig.class);
	}

	@PostConstruct
	public void postConstruct() {
		this.testRAMObjectToJson = this.appConfigConversionHelper.cdmAppConfigsOf(
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
