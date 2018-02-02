package image.exifweb.image;

import java.util.Date;

/**
 * Created by adr on 14-Apr-15.
 */
public interface ImageBasicInfo {
	public String getThumbPath();

	public void setThumbPath(String imagePath);

	public String getImagePath();

	public void setImagePath(String imagePath);

	public String getImgName();

	public void setImgName(String imgName);

	public String getAlbumName();

	public void setAlbumName(String albumName);

	public Date getThumbLastModified();

	public void setThumbLastModified(Date thumbLastModified);
}
