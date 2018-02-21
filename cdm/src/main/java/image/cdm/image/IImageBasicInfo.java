package image.cdm.image;

import java.util.Date;

/**
 * Created by adr on 14-Apr-15.
 */
public interface IImageBasicInfo {
	void setThumbPath(String imagePath);

	void setImagePath(String imagePath);

	String getImgName();

	String getAlbumName();

	Date getThumbLastModified();
	
	Date getDateTime();
}
