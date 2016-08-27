package image.exifweb.gallery3;

import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 5/14/14
 * Time: 9:39 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class Gallery3RestCtrl {
    private static final Logger logger = LoggerFactory.getLogger(Gallery3RestCtrl.class);
    @Value("${gallery3.base.url}")
    private String gallery3BaseUrl;
    @Value("${gallery3.albums.id}")
    private String gallery3AlbumsId;
    @Value("${gallery3.albums.name}")
    private String gallery3AlbumsName;
    @Value("${gallery3.thumbs.url}")
    private String gallery3ThumbsUrl;
    @Value("${gallery3.fullimage.url}")
    private String gallery3FullimageUrl;
    @Value("${gallery3.item.url}")
    private String gallery3ItemUrl;
    @Value("${gallery3.tree.url}")
    private String gallery3TreeUrl;
    @Inject
    private SessionFactory sessionFactory;

    @RequestMapping(value = {"/", "/rest"}, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> login(HttpSession httpSession) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(
            "1114d4023d89b15ce10a20ba4333eff7", headers, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(value = "/rest/tree/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    @ResponseBody
    public Gallery3TreeParent tree(@PathVariable String id, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        Gallery3TreeParent parent = new Gallery3TreeParent(gallery3BaseUrl +
            requestURI.substring(requestURI.indexOf("/rest/tree")));
        if (id.equals(gallery3AlbumsId)) {
            Session session = sessionFactory.getCurrentSession();
            Query q = session.createQuery("FROM Album ORDER BY name DESC");
            List<Album> albums = q.list();
            for (Album album : albums) {
                parent.getEntity().add(new Gallery3TreeEntity(album, gallery3ThumbsUrl, gallery3ItemUrl));
                parent.getMembers().add(gallery3TreeUrl + "album-" + album.getId());
            }
        } else {
            Session session = sessionFactory.getCurrentSession();
            Album album = (Album) session.get(Album.class,
                getDbPK(id));
            Query q = session.createQuery("SELECT i FROM Image i JOIN i.album a " +
                "WHERE a.id = :albumId AND i.deleted = 0 AND i.status = 0 " +
                "ORDER BY i.dateTimeOriginal ASC");
            q.setInteger("albumId", getDbPK(id));
            List<Image> images = q.list();
            for (Image image : images) {
                parent.getEntity().add(new Gallery3TreeEntity(image,
                    album, gallery3FullimageUrl, gallery3ThumbsUrl, gallery3ItemUrl));
                parent.getMembers().add(gallery3TreeUrl + "photo-" + image.getId());
            }
        }
        return parent;
    }

    @RequestMapping(value = "/rest/item/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    @ResponseBody
    public Gallery3ItemParent item(@PathVariable String id, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        Gallery3ItemParent parent = new Gallery3ItemParent(gallery3BaseUrl +
            requestURI.substring(requestURI.indexOf("/rest/item")));
        parent.getEntity().put("id", id);
        parent.getEntity().put("type", getType(id));
        parent.getEntity().put("title", getTitle(parent));
        if (id.equals(gallery3AlbumsId)) {
            Session session = sessionFactory.getCurrentSession();
            Query q = session.createQuery("FROM Album ORDER BY name DESC");
            List<Album> albums = q.list();
            parent.setKidsCount(albums.size());
            for (Album album : albums) {
                parent.getMembers().add(new Gallery3Item(album, gallery3ThumbsUrl));
            }
        } else if (parent.getEntity().get("type").equals("album")) {
            Session session = sessionFactory.getCurrentSession();
            Album album = (Album) session.get(Album.class, getDbPK(id));
            Query q = session.createQuery("SELECT i FROM Image i JOIN i.album a " +
                "WHERE a.id = :albumId AND i.deleted = 0 AND i.status = 0 " +
                "ORDER BY i.dateTimeOriginal ASC");
            q.setInteger("albumId", getDbPK(id));
            List<Image> images = q.list();
            parent.setKidsCount(images.size());
            for (Image image : images) {
                parent.getMembers().add(new Gallery3Item(image, album, gallery3FullimageUrl, gallery3ThumbsUrl));
            }
        }
        return parent;
    }

    private Integer getDbPK(String id) {
        return new Integer(id.substring(id.indexOf('-') + 1));
    }

    private String getTitle(Gallery3ItemParent parent) {
        if (parent.getEntity().get("id").equals(gallery3AlbumsId)) {
            return gallery3AlbumsName;
        } else if (parent.getEntity().get("type").equals("album")) {
            Session session = sessionFactory.getCurrentSession();
            Album album = (Album) session.get(Album.class,
                getDbPK(parent.getEntity().get("id")));
            return album.getName();
        } else {
            Session session = sessionFactory.getCurrentSession();
            Image image = (Image) session.get(Image.class,
                getDbPK(parent.getEntity().get("id")));
            return image.getName();
        }
    }

    private String getType(String id) {
        if (id.equals(gallery3AlbumsId) || id.startsWith("album-")) {
            return "album";
        } else {
            return "photo";
        }
    }
}
