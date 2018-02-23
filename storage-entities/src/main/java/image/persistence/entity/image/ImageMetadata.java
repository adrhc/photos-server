package image.persistence.entity.image;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by adr on 2/10/18.
 */
@Embeddable
public class ImageMetadata implements Serializable {
	@Embedded
	private ExifData exifData = new ExifData();
	/**
	 * related to image's file change
	 * used for image's url (impact browser-cache)
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date dateTime;
	/**
	 * related to thumb's file change
	 * used for thumb's url (impact browser-cache)
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "thumb_last_modified", nullable = false)
	private Date thumbLastModified;

	/**
	 * required by hibernate
	 */
	public ImageMetadata() {
	}

	public ImageMetadata(Date dateTime) {
		this.dateTime = dateTime;
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

	public ExifData getExifData() {
		return exifData;
	}

	public void setExifData(ExifData exifData) {
		this.exifData = exifData;
	}

	@Override
	public String toString() {
		return "ImageMetadata{" +
				"dateTime=" + dateTime +
				", thumbLastModified=" + thumbLastModified +
				", exifData=" + exifData.toString() +
				'}';
	}
}
