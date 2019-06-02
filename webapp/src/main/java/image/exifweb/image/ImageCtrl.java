package image.exifweb.image;

import com.fasterxml.jackson.annotation.JsonView;
import image.cdm.image.ExifInfo;
import image.cdm.image.ImageRating;
import image.cdm.image.status.ImageStatus;
import image.jpa2x.repositories.AlbumRepository;
import image.jpa2x.repositories.ImageRepository;
import image.persistence.entity.Image;
import image.persistence.entity.jsonview.ImageViews;
import image.photos.image.ImageToExifInfoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

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
	@Autowired
	private AlbumRepository albumRepository;
	@Autowired
	private ImageRepository imageRepository;
	private ImageToExifInfoConverter metadataEntityToDTOConverter =
			new ImageToExifInfoConverter();

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@JsonView(ImageViews.Album.class)
	public Image takeById(@PathVariable Integer id, WebRequest webRequest) {
		Image image = this.imageRepository.takeById(id);
		if (webRequest.checkNotModified(
				image.getImageMetadata().getDateTime().getTime())) {
			return null;
		}
		return image;
	}

	@RequestMapping(value = "/exif/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public ExifInfo getExifById(@PathVariable Integer id, WebRequest webRequest) {
		Image image = this.imageRepository.getById(id);
		if (webRequest.checkNotModified(
				image.getImageMetadata().getDateTime().getTime())) {
			return null;
		}
		return this.metadataEntityToDTOConverter.convert(image);
	}

	@RequestMapping(value = "/changeStatus",
			method = {RequestMethod.POST, RequestMethod.OPTIONS},
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void changeStatus(@RequestBody ImageStatus imageStatus) {
		this.imageRepository.changeStatus(imageStatus);
	}

	@RequestMapping(value = "/setRating",
			method = {RequestMethod.POST, RequestMethod.OPTIONS},
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void changeRating(@RequestBody ImageRating imageRating) {
		this.imageRepository.changeRating(imageRating);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/putAlbumCover/{imageId}",
			method = {RequestMethod.POST, RequestMethod.OPTIONS},
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void putAlbumCover(@PathVariable Integer imageId) throws IOException {
		this.albumRepository.putAlbumCover(imageId);
	}
}
