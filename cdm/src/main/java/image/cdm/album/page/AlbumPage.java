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
 *
 * thumbLastModified is only used for computing thumb path
 * (not used in angular) so it makes no sense to serialize it!
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
	 * but not used in angular (so not save into json!)
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AlbumPage albumPage = (AlbumPage) o;

		if (hidden != albumPage.hidden) return false;
		if (personal != albumPage.personal) return false;
		if (ugly != albumPage.ugly) return false;
		if (duplicate != albumPage.duplicate) return false;
		if (printable != albumPage.printable) return false;
		if (imageHeight != albumPage.imageHeight) return false;
		if (imageWidth != albumPage.imageWidth) return false;
		if (rating != albumPage.rating) return false;
		if (isCover != albumPage.isCover) return false;
		if (!id.equals(albumPage.id)) return false;
		if (!imgName.equals(albumPage.imgName)) return false;
		if (albumName != null ? !albumName.equals(albumPage.albumName) : albumPage.albumName != null) return false;
		if (thumbPath != null ? !thumbPath.equals(albumPage.thumbPath) : albumPage.thumbPath != null) return false;
		if (imagePath != null ? !imagePath.equals(albumPage.imagePath) : albumPage.imagePath != null) return false;
		if (thumbLastModified != null ? !thumbLastModified.equals(albumPage.thumbLastModified) : albumPage.thumbLastModified != null)
			return false;
		if (!dateTime.equals(albumPage.dateTime)) return false;
		if (imageLastUpdate != null ? !imageLastUpdate.equals(albumPage.imageLastUpdate) : albumPage.imageLastUpdate != null)
			return false;
		return albumLastUpdate != null ? albumLastUpdate.equals(albumPage.albumLastUpdate) : albumPage.albumLastUpdate == null;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + imgName.hashCode();
		result = 31 * result + (hidden ? 1 : 0);
		result = 31 * result + (personal ? 1 : 0);
		result = 31 * result + (ugly ? 1 : 0);
		result = 31 * result + (duplicate ? 1 : 0);
		result = 31 * result + (printable ? 1 : 0);
		result = 31 * result + imageHeight;
		result = 31 * result + imageWidth;
		result = 31 * result + (albumName != null ? albumName.hashCode() : 0);
		result = 31 * result + (int) rating;
		result = 31 * result + (isCover ? 1 : 0);
		result = 31 * result + (thumbPath != null ? thumbPath.hashCode() : 0);
		result = 31 * result + (imagePath != null ? imagePath.hashCode() : 0);
		result = 31 * result + (thumbLastModified != null ? thumbLastModified.hashCode() : 0);
		result = 31 * result + dateTime.hashCode();
		result = 31 * result + (imageLastUpdate != null ? imageLastUpdate.hashCode() : 0);
		result = 31 * result + (albumLastUpdate != null ? albumLastUpdate.hashCode() : 0);
		return result;
	}
}
