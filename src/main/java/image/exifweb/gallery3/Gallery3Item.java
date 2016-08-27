package image.exifweb.gallery3;

import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 5/14/14
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class Gallery3Item implements Serializable {
    private String id;
    private String title;
    private String type;
    private String file_url_public;
    private String thumb_url_public;

    public Gallery3Item(Album album, String gallery3ThumbsUrl) {
        id = "album-" + album.getId();
        title = album.getName();
        type = "album";
//        thumb_url_public = gallery3ThumbsUrl + album.getName() + '/' + album.getPath();
    }

    public Gallery3Item(Image image, Album album, String gallery3FullimageUrl, String gallery3ThumbsUrl) {
        id = "photo-" + image.getId();
        title = image.getName();
        type = "photo";
        file_url_public = gallery3FullimageUrl + album.getName() + '/' + image.getName();
        thumb_url_public = gallery3ThumbsUrl + album.getName() + '/' + image.getName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFile_url_public() {
        return file_url_public;
    }

    public void setFile_url_public(String file_url_public) {
        this.file_url_public = file_url_public;
    }

    public String getThumb_url_public() {
        return thumb_url_public;
    }

    public void setThumb_url_public(String thumb_url_public) {
        this.thumb_url_public = thumb_url_public;
    }
}
