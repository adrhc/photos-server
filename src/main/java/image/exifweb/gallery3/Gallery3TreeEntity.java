package image.exifweb.gallery3;

import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 5/15/14
 * Time: 1:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class Gallery3TreeEntity implements Serializable {
    private String url;
    private Gallery3TreeEntityEntity entity;

    public Gallery3TreeEntity(Album album, String gallery3ThumbsUrl, String gallery3ItemUrl) {
        entity = new Gallery3TreeEntityEntity(album, gallery3ThumbsUrl, gallery3ItemUrl);
        url = gallery3ItemUrl + entity.getId();
    }

    public Gallery3TreeEntity(Image image, Album album, String gallery3FullimageUrl,
                              String gallery3ThumbsUrl, String gallery3ItemUrl) {
        entity = new Gallery3TreeEntityEntity(image, album,
            gallery3FullimageUrl, gallery3ThumbsUrl, gallery3ItemUrl);
        url = gallery3ItemUrl + entity.getId();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Gallery3TreeEntityEntity getEntity() {
        return entity;
    }

    public void setEntity(Gallery3TreeEntityEntity entity) {
        this.entity = entity;
    }
}
