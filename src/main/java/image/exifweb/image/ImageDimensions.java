package image.exifweb.image;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/13/14
 * Time: 12:36 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ImageDimensions extends Serializable {
	public int getImageHeight();

	public void setImageHeight(int imageHeight);

	public int getImageWidth();

	public void setImageWidth(int imageWidth);
}
