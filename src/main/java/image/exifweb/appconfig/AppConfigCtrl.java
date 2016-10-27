package image.exifweb.appconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import image.exifweb.persistence.AppConfig;
import image.exifweb.sys.AppConfigService;
import image.exifweb.sys.MailService;
import image.exifweb.sys.ProcessInfoService;
import image.exifweb.sys.process.ProcStatPercent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
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
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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
public class AppConfigCtrl {
    private static final Logger logger = LoggerFactory.getLogger(AppConfigCtrl.class);
    @Inject
    private ProcessInfoService processInfoService;
    @Inject
    private MailService mailService;
    @Inject
    private AppConfigService appConfigService;
    @Inject
    private MappingJackson2JsonView jacksonConverter;
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

    @RequestMapping(value = "/testRAMString", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String testRAMString()
            throws IOException, InterruptedException {
        return testRAMString;
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
    @CacheEvict(value = "appConfig", allEntries = true)
    public void reloadParams(Model model) {
        model.addAttribute("message", "App params reloaded!");
    }

    @RequestMapping(value = "/updateAppConfigs", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void update(@RequestBody List<AppConfig> appConfigs, Model model) throws IOException {
        appConfigService.update(appConfigs);
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
    public List<AppConfig> getAppConfigs(WebRequest webRequest, HttpServletRequest request) {
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
        return appConfigService.getAppConfigs();
    }

    @PostConstruct
    public void postConstruct() {
        testRAMObjectToJson = appConfigService.getAppConfigs();
        try {
            testRAMString = jacksonConverter.getObjectMapper().writeValueAsString(testRAMObjectToJson);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
