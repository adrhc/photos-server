package image.exifweb.album.page;

import image.cdm.album.page.AlbumPage;
import image.exifweb.util.date.IDateUtil;
import image.exifweb.web.controller.INotModifiedChecker;
import image.jpa2x.repositories.AppConfigRepository;
import image.persistence.repository.AlbumPageRepository;
import image.persistence.repository.ESortType;
import image.photos.album.AlbumPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by adr on 2/8/18.
 */
@RestController
@RequestMapping("/json/page")
public class AlbumPageCtrlImpl implements INotModifiedChecker, IDateUtil {
	private static final Logger logger = LoggerFactory.getLogger(AlbumPageCtrlImpl.class);
	@Autowired
	private AlbumPageRepository albumPageRepository;
	@Autowired
	private AppConfigRepository appConfigRepository;
	@Autowired
	private AlbumPageService albumPageService;

	/**
	 * Test without authorization:
	 * curl -H "Accept: application/json" "http://127.0.0.1:8080/exifweb/app/json/page/count?albumId=52&viewHidden=false"
	 * Test with authorization:
	 * curl -H "Accept: application/json" "http://127.0.0.1:8080/exifweb/app/json/page/count?albumId=52&viewHidden=true"
	 */
	@RequestMapping(value = "/count", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ADMIN') or !#viewHidden")
	public PageCount pageCount(
			@RequestParam(name = "albumId") Integer albumId,
			@RequestParam(name = "toSearch", required = false) String toSearch,
			@RequestParam(name = "viewHidden", defaultValue = "false") boolean viewHidden,
			@RequestParam(name = "viewOnlyPrintable", defaultValue = "false") boolean viewOnlyPrintable) {
		PageCount pageCount = new PageCount();
		pageCount.setPageCount(this.albumPageRepository.countPages(
				toSearch, viewHidden, viewOnlyPrintable, albumId));
		pageCount.setPhotosPerPage(this.appConfigRepository.getPhotosPerPage());
		logger.debug(pageCount.toString());
		return pageCount;
	}

	@PreAuthorize("hasRole('ADMIN') or !#viewHidden")
	@RequestMapping(method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public List<AlbumPage> page(
			@RequestParam(name = "albumId") Integer albumId,
			@RequestParam(name = "pageNr") int pageNr,
			@RequestParam(name = "sort", defaultValue = "asc") String sort,
			@RequestParam(name = "viewHidden", defaultValue = "false") boolean viewHidden,
			@RequestParam(name = "viewOnlyPrintable", defaultValue = "false") boolean viewOnlyPrintable,
			@RequestParam(name = "toSearch", required = false) String toSearch) {
		return this.albumPageService.getPage(pageNr,
				ESortType.valueOf(sort.toUpperCase()),
				toSearch, viewHidden, viewOnlyPrintable, albumId);
	}
}
