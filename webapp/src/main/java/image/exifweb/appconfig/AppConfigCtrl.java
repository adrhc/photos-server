package image.exifweb.appconfig;

import image.cdm.AppConfig;
import image.exifweb.util.procinfo.ProcStatPercent;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AppConfigCtrl {
	@RequestMapping("/subscribeToAsyncProcMemStats")
	@ResponseBody
	DeferredResult<Model> subscribeToAsyncProcMemStats();

	@RequestMapping(value = "/getProcMemStatSummary", produces = MediaType.APPLICATION_JSON_VALUE)
	void getProcMemStatSummary(Model model)
			throws IOException, InterruptedException;

	@RequestMapping(value = "/getProcMemFullStats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> getProcMemFullStats();

	@RequestMapping(value = "/testRAMObjectToJson", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	List<AppConfig> testRAMObjectToJson();

	@RequestMapping(value = "/testRAMObjectToJsonDeferred", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	DeferredResult<List<AppConfig>> testRAMObjectToJsonDeferred();

	@RequestMapping(value = "/testRAMString", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	String testRAMString();

	@RequestMapping(value = "/testRAMStringDeferred", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	DeferredResult<String> testRAMStringDeferred();

	@RequestMapping(value = "/getMemStat", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	List<ProcStatPercent> getMemStat() throws IOException, InterruptedException;

	@RequestMapping(value = "/getProcStat", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	List<ProcStatPercent> getProcStat() throws IOException, InterruptedException;

	@RequestMapping(value = "/gc", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	void gc(Model model);

	@RequestMapping(value = "/checkProcess", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	void checkProcess(@RequestParam String[] commands, Model model) throws Exception;

	@RequestMapping(value = "/checkMailService", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	void checkMailService(Model model) throws Exception;

	@RequestMapping(value = "/reloadParams", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	void reloadParams(Model model);

	@RequestMapping(value = "/updateAppConfigs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	void update(@RequestBody List<AppConfig> appConfigs, Model model) throws IOException;

	@RequestMapping(value = "/canUseJsonFiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	Map<String, String> canUseJsonFiles(WebRequest webRequest);

	@RequestMapping(value = "getAppConfigs",
			method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	List<AppConfig> getAppConfigs(WebRequest webRequest);

	@RequestMapping(value = "findAllOrderByNameAscNotCached",
			method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	List<AppConfig> findAllOrderByNameAscNotCached();

	@RequestMapping(value = "findByNameNotCached",
			method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	AppConfig findByNameNotCached();

	@PostConstruct
	void postConstruct();
}
