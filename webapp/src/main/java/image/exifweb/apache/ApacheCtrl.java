package image.exifweb.apache;

import image.exifweb.web.json.JsonStringValue;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * Created by adrian.petre on 18-06-2014.
 */
@RestController
@RequestMapping("/json/apache")
public class ApacheCtrl {
	public static final String LOG_TYPE_ACCESS = "access";

	@Inject
	private ApacheService apacheService;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(value = "/getApacheLog", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public JsonStringValue getApacheLog(@RequestParam String type,
			WebRequest webRequest) throws IOException {
		File file;
		if (type.equalsIgnoreCase(LOG_TYPE_ACCESS)) {
			file = this.apacheService.getAccessLogFile();
		} else {
			file = this.apacheService.getErrorLogFile();
		}
		if (webRequest.checkNotModified(file.lastModified())) {
			return null;
		}
		return new JsonStringValue(FileUtils.readFileToString(file, "UTF-8"));
	}
}
