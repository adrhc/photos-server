package image.exifweb.exif;

import image.exifweb.album.AlbumImporter;
import image.exifweb.util.deferredresult.KeyValueDeferredResult;
import image.exifweb.util.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:12 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/json/action/exif")
public class ExtractExifCtrl {
	private static final Logger logger = LoggerFactory.getLogger(ExtractExifCtrl.class);
	@Inject
	private ThreadPoolTaskExecutor asyncExecutor;
	@Inject
	private AlbumImporter albumImporter;

	@RequestMapping(method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public DeferredResult<Map<String, String>> reImport(@RequestBody JsonValue jsonValue) {
		logger.debug("BEGIN {}", jsonValue.getValue());
		return KeyValueDeferredResult.of((deferredResult) -> {
			if (StringUtils.hasText(jsonValue.getValue())) {
				albumImporter.importAlbumByName(jsonValue.getValue());
				deferredResult.setResult("message", "Reimported " + jsonValue.getValue());
			} else {
				albumImporter.importAllFromAlbumsRoot();
				deferredResult.setResult("message", "Reimported all albums");
			}
			logger.debug("[reImport] END {}", jsonValue.getValue());
		}, asyncExecutor);
	}
}
