package image.exifweb.image;

import image.exifweb.album.export.AlbumExporterService;
import image.exifweb.album.page.AlbumPage;
import image.exifweb.album.AlbumRepository;
import image.exifweb.persistence.Image;
import image.exifweb.sys.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/json/image")
public class ImageCtrl {
	private static final Logger logger = LoggerFactory.getLogger(ImageCtrl.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");
	@Inject
	private AlbumRepository albumRepository;
	@Inject
	private ImageService imageService;
	@Inject
	private AppConfigService appConfigService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Image getById(@PathVariable Integer id, WebRequest webRequest) {
		Image image = imageService.getById(id);
		if (webRequest.checkNotModified(image.getDateTime().getTime())) {
			return null;
		}
		return image;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or !#viewHidden")
	@RequestMapping(value = "/countPages", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public Callable<Model> pageCount(
			@RequestParam(name = "albumId") Integer albumId,
			@RequestParam(name = "toSearch", required = false) String toSearch,
			@RequestParam(name = "viewHidden", defaultValue = "false") boolean viewHidden,
			@RequestParam(name = "viewOnlyPrintable", defaultValue = "false") boolean viewOnlyPrintable,
			Model model) {
		return () -> {
			model.addAttribute(AlbumExporterService.PHOTOS_PER_PAGE, appConfigService.getPhotosPerPage());
			model.addAttribute(AlbumExporterService.PAGE_COUNT,
					albumRepository.getPageCount(toSearch, viewHidden, viewOnlyPrintable, albumId));
			return model;
		};
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or !#viewHidden")
	@RequestMapping(value = "/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Callable<List<AlbumPage>> page(
			@RequestParam(name = "albumId") Integer albumId,
			@RequestParam(name = "pageNr") int pageNr,
			@RequestParam(name = "sort", defaultValue = "asc") String sort,
			@RequestParam(name = "viewHidden", defaultValue = "false") boolean viewHidden,
			@RequestParam(name = "viewOnlyPrintable", defaultValue = "false") boolean viewOnlyPrintable,
			@RequestParam(name = "toSearch", required = false) String toSearch,
			WebRequest webRequest) {
		return () -> {
			List<AlbumPage> albumPages =
					albumRepository.getPage(pageNr, sort, toSearch, viewHidden, viewOnlyPrintable, albumId);
			/*
			 * see also xhttp_zld.conf config (ngx.var.uri ~= /app/json/image/page) for:
			 * location /photos/app/
			 * location /photosj/app/
			 *
			 * ImageLastUpdate means the record in DB but not the actual file!
			 * ThumbLastModified is related to actual image file.
			 */
			Optional<Date> imageLastUpdate =
					albumPages.stream()
							.map(AlbumPage::getImageLastUpdate)
							.max(Date::compareTo);
			if (imageLastUpdate.isPresent()) {
				if (webRequest.checkNotModified(imageLastUpdate.get().getTime())) {
					return null;
				}
				logger.debug("page modified since: {}",
						sdf.format(imageLastUpdate.get()));
			} else {
				logger.debug("page modified since ever");
			}
			logger.debug("pageNr = {}, sort = {}, viewHidden = {}, " +
							"viewOnlyPrintable = {}, albumId = {}, toSearch = {}",
					pageNr, sort, viewHidden, viewOnlyPrintable, albumId, toSearch);
			return albumPages;
		};
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
