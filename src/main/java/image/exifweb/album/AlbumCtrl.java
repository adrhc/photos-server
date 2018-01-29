package image.exifweb.album;

import image.exifweb.album.events.AlbumEventsEmitter;
import image.exifweb.album.events.EAlbumEventType;
import image.exifweb.persistence.Album;
import image.exifweb.persistence.view.AlbumCover;
import image.exifweb.util.deferredresult.KeyValueDeferredResult;
import image.exifweb.util.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.DeferredResult;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/json/album")
public class AlbumCtrl {
    private static final Logger logger = LoggerFactory.getLogger(AlbumCtrl.class);
    @Inject
    private ThreadPoolTaskExecutor asyncExecutor;
    @Inject
    private AlbumService albumService;
    @Inject
    private AlbumExporter albumExporter;
    @Inject
    private AlbumImporter albumImporter;
    @Inject
    private AlbumEventsEmitter albumEventsEmitter;

    @RequestMapping(value = "/importAlbums", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public DeferredResult<Map<String, String>> importNewAlbumsOnly() {
        logger.debug("BEGIN");
        return KeyValueDeferredResult.of((deferredResult) -> {
            List<Album> newAlbums = new ArrayList<>();
            albumEventsEmitter.subscribe(EAlbumEventType.ALBUM_IMPORTED,
                    albumImporter.requestId.get(), ae -> newAlbums.add(ae.getAlbum()));
            albumImporter.importNewAlbumsOnly();
            logger.debug("BEGIN importedAlbums.size = {}", newAlbums.size());
            if (newAlbums.isEmpty()) {
                deferredResult.setResult("message", "No new album to import!");
            } else {
                deferredResult.setResult("message", "Albums imported for: " +
                        newAlbums.stream().map(Album::getName)
                                .collect(Collectors.joining(", ")));
            }
        }, asyncExecutor);
    }

    @RequestMapping(value = "/writeJsonForAlbumsPage", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public DeferredResult<Map<String, String>> updateJsonForAlbumsPage() {
        logger.debug("BEGIN");
        return KeyValueDeferredResult.of((deferredResult) -> {
            albumExporter.writeJsonForAlbumsPageSafe();
            deferredResult.setResult("message", AlbumExporter.ALBUMS_PAGE_JSON + " updated!");
        }, asyncExecutor);
    }

    @RequestMapping(value = "/updateJsonForAllAlbums", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public DeferredResult<Map<String, String>> updateJsonForAllAlbums() {
        logger.debug("BEGIN");
        return KeyValueDeferredResult.of((deferredResult) -> {
            switch (albumExporter.writeJsonForAllAlbumsSafe()) {
                case fail:
                    deferredResult.setResult("message", "All JSON files NOT updated!");
                    break;
                case success:
                    deferredResult.setResult("message", "All JSON files updated!");
                    break;
                case partial:
                    deferredResult.setResult("message", "Some JSON files updated some NOT!");
            }
        }, asyncExecutor);
    }

    @RequestMapping(value = "/updateJsonForAlbum", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public DeferredResult<Map<String, String>> updateJsonFor1Album(@RequestBody JsonValue jsonValue) {
        logger.debug("BEGIN");
        return KeyValueDeferredResult.of((deferredResult) -> {
            if (albumExporter.writeJsonForAlbumSafe(jsonValue.getValue())) {
                deferredResult.setResult("message",
                        "JSON files NOT updated for album " + jsonValue.getValue() + "!");
            } else {
                deferredResult.setResult("message",
                        "JSON files updated for album " + jsonValue.getValue() + "!");
            }
        }, asyncExecutor);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Album getAlbumById(@PathVariable Integer id, WebRequest webRequest) {
        Album album = albumService.getAlbumById(id);
        if (webRequest.checkNotModified(album.getLastUpdate().getTime())) {
            return null;
        }
        return album;
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<AlbumCover> getAllCovers(WebRequest webRequest) {
        if (webRequest.checkNotModified(albumService.getLastUpdatedForAlbums().getTime())) {
            return null;
        }
        return albumService.getAllCovers(true);
    }
}
