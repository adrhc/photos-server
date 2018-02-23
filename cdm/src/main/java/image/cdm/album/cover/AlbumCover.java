package image.cdm.album.cover;

import com.fasterxml.jackson.annotation.JsonIgnore;
import image.cdm.image.IImageDimensions;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/12/14
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlbumCover implements IImageDimensions, Serializable {
	private Integer id;
	private String albumName;
	private String imgName;
	private int imageHeight;
	private int imageWidth;
	private boolean dirty;
	private String thumbPath;
	@JsonIgnore
	private Date lastUpdate;

	/**
	 * used by AppConfigJsonTest
	 */
	public AlbumCover() {
		// empty
	}

	public AlbumCover(Integer albumId, String albumName, boolean dirty, Date lastUpdate) {
		this.id = albumId;
		this.albumName = albumName;
		this.dirty = dirty;
		this.lastUpdate = lastUpdate;
	}

	public AlbumCover(Integer albumId, String albumName, String imgName,
	                  int imageHeight, int imageWidth, boolean dirty, Date lastUpdate) {
		this(albumId, albumName, dirty, lastUpdate);
		this.imgName = imgName;
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
	}

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

	public String getThumbPath() {
		return thumbPath;
	}

	public void setThumbPath(String thumbPath) {
		this.thumbPath = thumbPath;
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

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
