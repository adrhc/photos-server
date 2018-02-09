package image.exifweb.album.page;

import image.exifweb.album.export.AlbumExporterService;
import image.exifweb.appconfig.AppConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Created by adr on 2/8/18.
 */
@RestController
@RequestMapping("/json/page")
public class AlbumPageCtrl {
	private static final Logger logger = LoggerFactory.getLogger(AlbumPageCtrl.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");
	@Inject
	private AlbumPageRepository albumPageRepository;
	@Inject
	private AppConfigService appConfigService;
	@Inject
	private AlbumPageService albumPageService;

	@PreAuthorize("hasRole('ROLE_ADMIN') or !#viewHidden")
	@RequestMapping(value = "/count", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Callable<Model> pageCount(
			@RequestParam(name = "albumId") Integer albumId,
			@RequestParam(name = "toSearch", required = false) String toSearch,
			@RequestParam(name = "viewHidden", defaultValue = "false") boolean viewHidden,
			@RequestParam(name = "viewOnlyPrintable", defaultValue = "false") boolean viewOnlyPrintable,
			Model model) {
		return () -> {
			model.addAttribute(AlbumExporterService.PHOTOS_PER_PAGE,
					appConfigService.getPhotosPerPage());
			model.addAttribute(AlbumExporterService.PAGE_COUNT,
					albumPageRepository.getPageCount(toSearch,
							viewHidden, viewOnlyPrintable, albumId));
			return model;
		};
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or !#viewHidden")
	@RequestMapping(method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
					albumPageService.getPage(pageNr, sort, toSearch, viewHidden, viewOnlyPrintable, albumId);
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
				logger.debug("page modified since: {}", sdf.format(imageLastUpdate.get()));
			} else {
				logger.debug("page modified since ever");
			}
			logger.debug("pageNr = {}, sort = {}, viewHidden = {}, " +
							"viewOnlyPrintable = {}, albumId = {}, toSearch = {}",
					pageNr, sort, viewHidden, viewOnlyPrintable, albumId, toSearch);
			return albumPages;
		};
	}
}
