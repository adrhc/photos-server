package image.cdm.album.page;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import image.cdm.ICdmEntity;
import image.cdm.image.feature.IImageBasicInfo;
import image.cdm.image.feature.IImageDimensions;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/12/14
 * Time: 10:52 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = {"thumbLastModified"})
public class AlbumPage implements IImageBasicInfo, IImageDimensions, ICdmEntity {
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
	/**
	 * not related to imageLastUpdate but should
	 */
	private boolean isCover;
	private String thumbPath;
	private String imagePath;
	/**
	 * related to thumb's file change
	 * used for thumb's url (impact browser-cache)
	 */
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Date thumbLastModified;
	/**
	 * related to image's file change
	 * used for image's url (impact browser-cache)
	 */
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Date dateTime;
	/*
	 * is a @Version field so is related to db record change
	 */
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Date imageLastUpdate;
	/*
	 * is a @Version field so is related to db record change
	 */
	@JsonFormat(shape = JsonFormat.Shape.NUMBER)
	private Date albumLastUpdate;

	/**
	 * used by AlbumPageJsonTest
	 */
	public AlbumPage() {
		// empty
	}

	public AlbumPage(Integer id, String imgName, boolean hidden, boolean personal,
			boolean ugly, boolean duplicate, boolean printable, int imageHeight,
			int imageWidth, byte rating, Integer coverId, Date thumbLastModified,
			Date dateTime, Date imageLastUpdate, String albumName, Date albumLastUpdate) {
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
		this.isCover = id.equals(coverId);
		this.thumbLastModified = thumbLastModified;
		this.dateTime = dateTime;
		this.imageLastUpdate = imageLastUpdate;
		this.albumName = albumName;
		this.albumLastUpdate = albumLastUpdate;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String getImgName() {
		return this.imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	@Override
	public String getAlbumName() {
		return this.albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isPersonal() {
		return this.personal;
	}

	public void setPersonal(boolean personal) {
		this.personal = personal;
	}

	public boolean isUgly() {
		return this.ugly;
	}

	public void setUgly(boolean ugly) {
		this.ugly = ugly;
	}

	public boolean isDuplicate() {
		return this.duplicate;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	public boolean isPrintable() {
		return this.printable;
	}

	public void setPrintable(boolean printable) {
		this.printable = printable;
	}

	@Override
	public int getImageHeight() {
		return this.imageHeight;
	}

	@Override
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	@Override
	public int getImageWidth() {
		return this.imageWidth;
	}

	@Override
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public byte getRating() {
		return this.rating;
	}

	public void setRating(byte rating) {
		this.rating = rating;
	}

	public boolean isCover() {
		return this.isCover;
	}

	public void setCover(boolean cover) {
		this.isCover = cover;
	}

	@Override
	public Date getThumbLastModified() {
		return this.thumbLastModified;
	}

	public void setThumbLastModified(Date thumbLastModified) {
		this.thumbLastModified = thumbLastModified;
	}

	@Override
	public Date getDateTime() {
		return this.dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getThumbPath() {
		return this.thumbPath;
	}

	@Override
	public void setThumbPath(String thumbPath) {
		this.thumbPath = thumbPath;
	}

	public String getImagePath() {
		return this.imagePath;
	}

	@Override
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Date getImageLastUpdate() {
		return this.imageLastUpdate;
	}

	public void setImageLastUpdate(Date imageLastUpdate) {
		this.imageLastUpdate = imageLastUpdate;
	}

	public Date getAlbumLastUpdate() {
		return this.albumLastUpdate;
	}

	public void setAlbumLastUpdate(Date albumLastUpdate) {
		this.albumLastUpdate = albumLastUpdate;
	}
}
