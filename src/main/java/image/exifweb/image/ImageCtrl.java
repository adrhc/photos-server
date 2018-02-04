package image.exifweb.image;

import image.exifweb.album.AlbumExporter;
import image.exifweb.album.AlbumPage;
import image.exifweb.album.AlbumService;
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
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
	@Inject
	private AlbumService albumService;
	@Inject
	private ImageService imageService;
	@Inject
	private AppConfigService appConfigService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Image get(@PathVariable Integer id, WebRequest webRequest) {
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
		return new CallablePageCount(albumId, model, viewHidden, viewOnlyPrintable, toSearch);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or !#viewHidden")
	@RequestMapping(value = "/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public CallablePage page(
			@RequestParam(name = "albumId") Integer albumId,
			@RequestParam(name = "pageNr") int pageNr,
			@RequestParam(name = "sort", defaultValue = "asc") String sort,
			@RequestParam(name = "viewHidden", defaultValue = "false") boolean viewHidden,
			@RequestParam(name = "viewOnlyPrintable", defaultValue = "false") boolean viewOnlyPrintable,
			@RequestParam(name = "toSearch", required = false) String toSearch,
			WebRequest webRequest) {
		return new CallablePage(pageNr, sort, toSearch,
				viewHidden, viewOnlyPrintable, albumId, webRequest);
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
		albumService.putAlbumCover(imageId);
	}

	protected class CallablePageCount implements Callable<Model> {
		private Integer albumId;
		private Model model;
		private boolean viewHidden;
		private boolean viewOnlyPrintable;
		private String toSearch;

		public CallablePageCount(Integer albumId, Model model, boolean viewHidden,
		                         boolean viewOnlyPrintable, String toSearch) {
			this.albumId = albumId;
			this.model = model;
			this.viewHidden = viewHidden;
			this.viewOnlyPrintable = viewOnlyPrintable;
			this.toSearch = toSearch;
		}

		@Override
		public Model call() throws Exception {
			model.addAttribute(AlbumExporter.PHOTOS_PER_PAGE, appConfigService.getPhotosPerPage());
			model.addAttribute(AlbumExporter.PAGE_COUNT,
					albumService.getPageCount(toSearch, viewHidden, viewOnlyPrintable, albumId));
			return model;
		}
	}

	protected class CallablePage implements Callable<List<AlbumPage>> {
		private WebRequest webRequest;
		private Integer albumId;
		private String sort;
		private boolean viewHidden;
		private boolean viewOnlyPrintable;
		private String toSearch;
		private int pageNr = -1;

		public CallablePage(int pageNr, String sort, String toSearch,
		                    boolean viewHidden, boolean viewOnlyPrintable,
		                    Integer albumId, WebRequest webRequest) {
			this.albumId = albumId;
			this.pageNr = pageNr;
			this.sort = sort;
			this.viewHidden = viewHidden;
			this.viewOnlyPrintable = viewOnlyPrintable;
			this.toSearch = toSearch;
			this.webRequest = webRequest;
		}

		@Override
		public List<AlbumPage> call() throws Exception {
			List<AlbumPage> albumPages =
					albumService.getPage(pageNr, sort, toSearch, viewHidden, viewOnlyPrintable, albumId);
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
		}
	}
}
