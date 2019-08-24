package image.exifweb.album.export;

import image.exifweb.web.json.JsonStringValue;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

public interface AlbumExporterCtrl {
	@RequestMapping(value = "/writeJsonForAlbumsPage", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	DeferredResult<Map<String, String>> updateJsonForAlbumsPage();

	@RequestMapping(value = "/updateJsonForAllAlbums", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	DeferredResult<Map<String, String>> updateJsonForAllAlbums();

	@RequestMapping(value = "/updateJsonForAlbum", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	DeferredResult<Map<String, String>> updateJsonFor1Album(@RequestBody JsonStringValue jsonStringValue);
}
