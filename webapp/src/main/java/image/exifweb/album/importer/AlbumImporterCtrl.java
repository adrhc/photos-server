package image.exifweb.album.importer;

import image.exifweb.web.json.JsonStringValue;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

public interface AlbumImporterCtrl {
	@RequestMapping(value = "/reImport", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	DeferredResult<Map<String, String>> reImport(@RequestBody JsonStringValue json1Value);

	@RequestMapping(value = "/importNewAlbumsOnly", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	DeferredResult<Map<String, String>> importNewAlbumsOnly();
}
