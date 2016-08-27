package image.exifweb.gallery3;

import image.exifweb.persistence.Album;
import image.exifweb.persistence.Image;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 5/15/14
 * Time: 1:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class Gallery3TreeEntityEntity implements Serializable {
    private String id;
    private String title;
    private String slug;
    private String type;
    private String parent;
    private String file_url_public;
    private String thumb_url_public;
    private String sort_column = "dateTimeOriginal";
    private String sort_order = "ASC";
    private String captured = null;
    private String created = "1311331999";
    private String description = "";
    private String height = null;
    private String level = "2";
    private String mime_type = null;
    private String name = "Gemeinsame-Alben";
    private String owner_id = "2";
    private String rand_key = "0.5967710000";
    private String resize_height = null;
    private String resize_width = null;
    private String thumb_height = "132";
    private String thumb_width = "200";
    private String updated = "1311332015";
    private String view_count = "45";
    private String width = null;
    private String view_1 = "1";
    private String view_2 = "1";
    private String view_12 = "1";
    private String view_13 = "1";
    private String view_14 = "1";
    private String view_15 = "1";
    private String view_16 = "1";
    private String view_17 = "1";
    private String view_18 = "1";
    private String view_19 = "1";
    private String view_20 = "1";
    private String album_cover = "http://adrhc.asuscomm.com/pkg/gallery/index.php/rest/item/801";
    private String web_url = "http://adrhc.asuscomm.com/pkg/gallery/index.php/Gemeinsame-Alben";
    private String thumb_url = "http://adrhc.asuscomm.com/pkg/gallery/index.php/rest/data/1264?size=thumb";
    private int thumb_size = 4747;
    private boolean can_edit = false;

    public Gallery3TreeEntityEntity(Album album, String gallery3ThumbsUrl, String gallery3ItemUrl) {
        id = "album-" + album.getId();
        title = album.getName();
        slug = title;
        type = "album";
//        thumb_url_public = gallery3ThumbsUrl + album.getName() + '/' + album.getPath();
        file_url_public = thumb_url_public;
        parent = gallery3ItemUrl + "album-0";
    }

    public Gallery3TreeEntityEntity(Image image, Album album, String gallery3FullimageUrl,
                                    String gallery3ThumbsUrl, String gallery3ItemUrl) {
        id = "photo-" + image.getId();
        title = image.getName();
        type = "photo";
        thumb_url_public = gallery3ThumbsUrl + album.getName() + '/' + image.getName();
        file_url_public = gallery3FullimageUrl + album.getName() + '/' + image.getName();
        parent = gallery3ItemUrl + "album-0";
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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSort_column() {
        return sort_column;
    }

    public void setSort_column(String sort_column) {
        this.sort_column = sort_column;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }

    public String getCaptured() {
        return captured;
    }

    public void setCaptured(String captured) {
        this.captured = captured;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getRand_key() {
        return rand_key;
    }

    public void setRand_key(String rand_key) {
        this.rand_key = rand_key;
    }

    public String getResize_height() {
        return resize_height;
    }

    public void setResize_height(String resize_height) {
        this.resize_height = resize_height;
    }

    public String getResize_width() {
        return resize_width;
    }

    public void setResize_width(String resize_width) {
        this.resize_width = resize_width;
    }

    public String getThumb_height() {
        return thumb_height;
    }

    public void setThumb_height(String thumb_height) {
        this.thumb_height = thumb_height;
    }

    public String getThumb_width() {
        return thumb_width;
    }

    public void setThumb_width(String thumb_width) {
        this.thumb_width = thumb_width;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getView_1() {
        return view_1;
    }

    public void setView_1(String view_1) {
        this.view_1 = view_1;
    }

    public String getView_2() {
        return view_2;
    }

    public void setView_2(String view_2) {
        this.view_2 = view_2;
    }

    public String getView_12() {
        return view_12;
    }

    public void setView_12(String view_12) {
        this.view_12 = view_12;
    }

    public String getView_13() {
        return view_13;
    }

    public void setView_13(String view_13) {
        this.view_13 = view_13;
    }

    public String getView_14() {
        return view_14;
    }

    public void setView_14(String view_14) {
        this.view_14 = view_14;
    }

    public String getView_15() {
        return view_15;
    }

    public void setView_15(String view_15) {
        this.view_15 = view_15;
    }

    public String getView_16() {
        return view_16;
    }

    public void setView_16(String view_16) {
        this.view_16 = view_16;
    }

    public String getView_17() {
        return view_17;
    }

    public void setView_17(String view_17) {
        this.view_17 = view_17;
    }

    public String getView_18() {
        return view_18;
    }

    public void setView_18(String view_18) {
        this.view_18 = view_18;
    }

    public String getView_19() {
        return view_19;
    }

    public void setView_19(String view_19) {
        this.view_19 = view_19;
    }

    public String getView_20() {
        return view_20;
    }

    public void setView_20(String view_20) {
        this.view_20 = view_20;
    }

    public String getAlbum_cover() {
        return album_cover;
    }

    public void setAlbum_cover(String album_cover) {
        this.album_cover = album_cover;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public int getThumb_size() {
        return thumb_size;
    }

    public void setThumb_size(int thumb_size) {
        this.thumb_size = thumb_size;
    }

    public boolean isCan_edit() {
        return can_edit;
    }

    public void setCan_edit(boolean can_edit) {
        this.can_edit = can_edit;
    }
}
