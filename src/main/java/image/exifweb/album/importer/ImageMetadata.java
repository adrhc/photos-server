package image.exifweb.album.importer;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by adr on 2/10/18.
 */
@Embeddable
public class ImageMetadata implements Serializable {
	/**
	 * utilizat in url-ul imaginii si cu impact in browser-cache
	 */
	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date dateTime;
	/**
	 * related to image file change
	 * used for thumb's url (impact browser-cache)
	 */
	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "thumb_last_modified", nullable = false)
	private Date thumbLastModified;
	@Embedded
	private ExifData exifData = new ExifData();

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
}
