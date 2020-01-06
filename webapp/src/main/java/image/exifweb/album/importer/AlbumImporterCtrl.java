package image.exifweb.album.importer;

import image.exifweb.web.json.JsonStringValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

public interface AlbumImporterCtrl {
	DeferredResult<Map<String, String>> reImport(@RequestBody JsonStringValue json1Value);

	DeferredResult<Map<String, String>> importNewAlbumsOnly();
}
