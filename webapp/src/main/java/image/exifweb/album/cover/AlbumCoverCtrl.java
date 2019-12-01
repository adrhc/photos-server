package image.exifweb.album.cover;

import image.cdm.album.cover.AlbumCover;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

public interface AlbumCoverCtrl {
	@RequestMapping(method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	List<AlbumCover> getAllCovers(WebRequest webRequest);

	@RequestMapping(value = "/{id}", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	AlbumCover getAlbumCoverById(@PathVariable Integer id, WebRequest webRequest);

	@RequestMapping(value = "/search", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	AlbumCover searchAlbumCover(@RequestParam String name, WebRequest webRequest);
}
