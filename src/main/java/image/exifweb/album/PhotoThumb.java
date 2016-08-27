package image.exifweb.album;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import image.exifweb.image.ImageThumb;
import org.hibernate.annotations.Formula;

import javax.persistence.Transient;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/12/14
 * Time: 10:52 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = {"thumbLastModified"})
public class PhotoThumb implements ImageThumb {
    private Integer id;// image id
    private String imgName;
    @Formula("status & 1")
    private boolean hidden;
    @Formula("status & 2")
    private boolean personal;
    @Formula("status & 4")
    private boolean ugly;
    @Formula("status & 8")
    private boolean duplicate;
    private int imageHeight;
    private int imageWidth;
    private String albumName;
    private byte rating;
    private boolean isCover;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date thumbLastModified;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date dateTime;
    @Transient
    private String imgPath;

    public PhotoThumb(Integer id, String imgName, boolean hidden, boolean personal,
                      boolean ugly, boolean duplicate, int imageHeight, int imageWidth,
                      byte rating, Integer coverId, Date thumbLastModified, Date dateTime,
                      String albumName) {
        this.id = id;
        this.imgName = imgName;
        this.hidden = hidden;
        this.personal = personal;
        this.ugly = ugly;
        this.duplicate = duplicate;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.rating = rating;
        this.isCover = coverId != null && id.equals(coverId);
        this.thumbLastModified = thumbLastModified;
        this.dateTime = dateTime;
        this.albumName = albumName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isPersonal() {
        return personal;
    }

    public void setPersonal(boolean personal) {
        this.personal = personal;
    }

    public boolean isUgly() {
        return ugly;
    }

    public void setUgly(boolean ugly) {
        this.ugly = ugly;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
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

    public byte getRating() {
        return rating;
    }

    public void setRating(byte rating) {
        this.rating = rating;
    }

    public boolean isCover() {
        return isCover;
    }

    public void setCover(boolean cover) {
        isCover = cover;
    }

    public Date getThumbLastModified() {
        return thumbLastModified;
    }

    public void setThumbLastModified(Date thumbLastModified) {
        this.thumbLastModified = thumbLastModified;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
