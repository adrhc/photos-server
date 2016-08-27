package image.exifweb.persistence.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import image.exifweb.image.ImageThumb;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/12/14
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "v_album_cover")
@JsonIgnoreProperties(ignoreUnknown = true,
    value = {"hibernateLazyInitializer", "handler", "thumbLastModified"})
public class AlbumCover implements ImageThumb {
    @Id
    private Integer id;
    @Column(nullable = false, unique = true)
    private String albumName;
    @Column(nullable = false)
    private String imgName;
    @Column(nullable = false)
    private int imageHeight;
    @Column(nullable = false)
    private int imageWidth;
    @Column(name = "dirty")
    private boolean dirty;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "thumb_last_modified")
    private Date thumbLastModified;
    @Transient
    private String imgPath;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public Date getThumbLastModified() {
        return thumbLastModified;
    }

    public void setThumbLastModified(Date thumbLastModified) {
        this.thumbLastModified = thumbLastModified;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }
}
