package image.cdm.album.page;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import image.cdm.image.IImageBasicInfo;
import image.cdm.image.IImageDimensions;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/12/14
 * Time: 10:52 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = {"thumbLastModified"})
public class AlbumPage implements IImageBasicInfo, IImageDimensions, Serializable {
	private Integer id;// image id
	private String imgName;
	private boolean hidden;
	private boolean personal;
	private boolean ugly;
	private boolean duplicate;
	private boolean printable;
	private int imageHeight;
	private int imageWidth;
	private String albumName;
	private byte rating;
	private boolean isCover;
	private String thumbPath;
	private String imagePath;
	/**
	 * related to image file change
	 * used for thumb's url (impact browser-cache)
	 */
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Date thumbLastModified;
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Date dateTime;
	/*
	 * related to db record -> rating, status, deleted change
	 */
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Date imageLastUpdate;

	/**
	 * used by AlbumPageJsonTest
	 */
	public AlbumPage() {
		// empty
	}

	public AlbumPage(Integer id, String imgName, boolean hidden, boolean personal,
	                 boolean ugly, boolean duplicate, boolean printable, int imageHeight,
	                 int imageWidth, byte rating, Integer coverId, Date thumbLastModified,
	                 Date dateTime, String albumName, Date imageLastUpdate) {
		this.id = id;
		this.imgName = imgName;
		this.hidden = hidden;
		this.personal = personal;
		this.ugly = ugly;
		this.duplicate = duplicate;
		this.printable = printable;
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		this.rating = rating;
		this.isCover = coverId != null && id.equals(coverId);
		this.thumbLastModified = thumbLastModified;
		this.dateTime = dateTime;
		this.albumName = albumName;
		this.imageLastUpdate = imageLastUpdate;
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

	public boolean isPrintable() {
		return printable;
	}

	public void setPrintable(boolean printable) {
		this.printable = printable;
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

	public String getThumbPath() {
		return thumbPath;
	}

	@Override
	public void setThumbPath(String thumbPath) {
		this.thumbPath = thumbPath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Date getImageLastUpdate() {
		return imageLastUpdate;
	}

	public void setImageLastUpdate(Date imageLastUpdate) {
		this.imageLastUpdate = imageLastUpdate;
	}
}
