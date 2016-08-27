package image.exifweb.image;

import java.util.Date;

/**
 * Created by adr on 14-Apr-15.
 */
public interface ImageThumb extends ImageDimensions {
    public String getImgPath();

    public void setImgPath(String imgPath);

    public String getImgName();

    public void setImgName(String imgName);

    public String getAlbumName();

    public void setAlbumName(String albumName);

    public Date getThumbLastModified();

    public void setThumbLastModified(Date thumbLastModified);
}
