package image.exifweb.image;

import image.exifweb.album.AlbumRepository;
import image.exifweb.persistence.Image;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/json/image")
public class ImageCtrl {
	@Inject
	private AlbumRepository albumRepository;
	@Inject
	private ImageService imageService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public Image getById(@PathVariable Integer id, WebRequest webRequest) {
		Image image = imageService.getById(id);
		if (webRequest.checkNotModified(image.getDateTime().getTime())) {
			return null;
		}
		return image;
	}

	@RequestMapping(value = "/changeStatus",
			method = {RequestMethod.POST, RequestMethod.OPTIONS},
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void changeStatus(@RequestBody ImageStatus imageStatus) {
		imageService.changeStatus(imageStatus);
	}

	@RequestMapping(value = "/setRating",
			method = {RequestMethod.POST, RequestMethod.OPTIONS},
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void changeRating(@RequestBody ImageRating imageRating) {
		imageService.changeRating(imageRating);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/putAlbumCover/{imageId}",
			method = {RequestMethod.POST, RequestMethod.OPTIONS},
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void putAlbumCover(@PathVariable Integer imageId) throws IOException {
		albumRepository.putAlbumCover(imageId);
	}
}
