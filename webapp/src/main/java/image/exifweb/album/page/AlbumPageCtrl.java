package image.exifweb.album.page;

import image.cdm.album.page.AlbumPage;
import image.exifweb.util.date.IDateUtil;
import image.exifweb.web.controller.INotModifiedChecker;
import image.persistence.repository.AlbumPageRepository;
import image.persistence.repository.AlbumRepository;
import image.persistence.repository.AppConfigRepository;
import image.persistence.repository.ESortType;
import image.photos.album.AlbumPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by adr on 2/8/18.
 */
@RestController
@RequestMapping("/json/page")
public class AlbumPageCtrl implements INotModifiedChecker, IDateUtil {
    private static final Logger logger = LoggerFactory.getLogger(AlbumPageCtrl.class);
    @Inject
    private AlbumPageRepository albumPageRepository;
    @Inject
    private AppConfigRepository appConfigRepository;
    @Inject
    private AlbumPageService albumPageService;
    @Inject
    private AlbumRepository albumRepository;

    /**
     * Test without authorization:
     * curl -H "Accept: application/json" "http://127.0.0.1:8080/exifweb/app/json/page/count?albumId=52&viewHidden=false"
     * Test with authorization:
     * curl -H "Accept: application/json" "http://127.0.0.1:8080/exifweb/app/json/page/count?albumId=52&viewHidden=true"
     */
    @RequestMapping(value = "/count", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or !#viewHidden")
    public PageCount pageCount(
            @RequestParam(name = "albumId") Integer albumId,
            @RequestParam(name = "toSearch", required = false) String toSearch,
            @RequestParam(name = "viewHidden", defaultValue = "false") boolean viewHidden,
            @RequestParam(name = "viewOnlyPrintable", defaultValue = "false") boolean viewOnlyPrintable) {
        PageCount pageCount = new PageCount();
        pageCount.setPageCount(this.albumPageRepository.getPageCount(
                toSearch, viewHidden, viewOnlyPrintable, albumId));
        pageCount.setPhotosPerPage(this.appConfigRepository.getPhotosPerPage());
        logger.debug(pageCount.toString());
        return pageCount;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or !#viewHidden")
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<AlbumPage> page(
            @RequestParam(name = "albumId") Integer albumId,
            @RequestParam(name = "pageNr") int pageNr,
            @RequestParam(name = "sort", defaultValue = "asc") String sort,
            @RequestParam(name = "viewHidden", defaultValue = "false") boolean viewHidden,
            @RequestParam(name = "viewOnlyPrintable", defaultValue = "false") boolean viewOnlyPrintable,
            @RequestParam(name = "toSearch", required = false) String toSearch,
            WebRequest webRequest) {
        INotModifiedChecker _this = this;
        return _this.checkNotModified(
                () -> this.albumPageService.getPage(pageNr,
                        ESortType.valueOf(sort.toUpperCase()),
                        toSearch, viewHidden, viewOnlyPrintable, albumId),
                albumPages -> {
                    /*
                     * see also xhttp_zld.conf config (ngx.var.uri ~= /app/json/image/page) for:
                     * location /photos/app/
                     * location /photosj/app/
                     *
                     * ImageLastUpdate means the record in DB (@Version) instead of actual file!
                     * ThumbLastModified is related to actual image file.
                     */
                    Optional<Date> imageLastUpdate = albumPages.stream()
                            .flatMap(ap -> Stream.of(ap.getImageLastUpdate(), ap.getAlbumLastUpdate()))
                            .max(Date::compareTo);
                    // e.g. album's cover might change so the page
                    // might no longer contain the album's cover image
                    return imageLastUpdate.orElseGet(Date::new);
                }, webRequest);
    }
}
