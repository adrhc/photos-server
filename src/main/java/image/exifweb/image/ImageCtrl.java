package image.exifweb.image;

import image.exifweb.album.AlbumExporter;
import image.exifweb.album.AlbumService;
import image.exifweb.album.PhotoThumb;
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
import java.util.List;
import java.util.OptionalLong;
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
	public Callable<Model> pageCount(@RequestParam Integer albumId, Model model,
	                                 @RequestParam(defaultValue = "false") boolean viewHidden,
	                                 @RequestParam(required = false) String toSearch) {
		return new CallablePageCount(albumId, model, viewHidden, toSearch);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or !#viewHidden")
	@RequestMapping(value = "/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public CallablePage page(@RequestParam Integer albumId,
	                         @RequestParam int pageNr,
	                         @RequestParam(defaultValue = "asc") String sort,
	                         @RequestParam(defaultValue = "false") boolean viewHidden,
	                         @RequestParam(required = false) String toSearch,
	                         WebRequest webRequest) {
		return new CallablePage(pageNr, sort, toSearch, viewHidden, albumId, webRequest);
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
		private String toSearch;

		public CallablePageCount(Integer albumId, Model model, boolean viewHidden, String toSearch) {
			this.albumId = albumId;
			this.model = model;
			this.viewHidden = viewHidden;
			this.toSearch = toSearch;
		}

		@Override
		public Model call() throws Exception {
			model.addAttribute(AlbumExporter.PHOTOS_PER_PAGE, appConfigService.getPhotosPerPage());
			model.addAttribute(AlbumExporter.PAGE_COUNT,
					albumService.getPageCount(toSearch, viewHidden, albumId));
			return model;
		}
	}

	protected class CallablePage implements Callable<List<PhotoThumb>> {
		private WebRequest webRequest;
		private Integer albumId;
		private String sort;
		private boolean viewHidden;
		private String toSearch;
		private int pageNr = -1;

		public CallablePage(int pageNr, String sort, String toSearch,
		                    boolean viewHidden, Integer albumId, WebRequest webRequest) {
			this.albumId = albumId;
			this.pageNr = pageNr;
			this.sort = sort;
			this.viewHidden = viewHidden;
			this.toSearch = toSearch;
			this.webRequest = webRequest;
		}

		@Override
		public List<PhotoThumb> call() throws Exception {
			List<PhotoThumb> photoThumbs =
					albumService.getPage(pageNr, sort, toSearch, viewHidden, albumId);
			/*
			 * see also xhttp_zld.conf config (ngx.var.uri ~= /app/json/image/page) for:
			 * location /photos/app/
			 * location /photosj/app/
			 */
			OptionalLong thumbLastModified =
					photoThumbs.stream()
							.mapToLong(pt -> pt.getThumbLastModified().getTime())
							.max();
			if (thumbLastModified.isPresent()) {
				if (webRequest.checkNotModified(thumbLastModified.getAsLong())) {
					return null;
				}
			}
			return photoThumbs;
		}
	}
}
