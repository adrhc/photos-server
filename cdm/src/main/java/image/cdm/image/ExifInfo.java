package image.cdm.image;

import com.fasterxml.jackson.annotation.JsonFormat;
import image.persistence.entity.image.ExifData;

import java.util.Date;

/**
 * Created by adr on 2/10/18.
 */
public class ExifInfo extends ExifData {
	private Integer id;
	private String name;
	/**
	 * overrides ExifData.dateTimeOriginal in order to use JsonFormat
	 */
	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
	private Date dateTimeOriginal;
	/**
	 * utilizat in url-ul imaginii si cu impact in browser-cache
	 */
	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
	private Date dateTime;
	/**
	 * related to image file change
	 * used for thumb's url (impact browser-cache)
	 */
	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
	private Date thumbLastModified;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public Date getThumbLastModified() {
		return thumbLastModified;
	}

	public void setThumbLastModified(Date thumbLastModified) {
		this.thumbLastModified = thumbLastModified;
	}

	public Date getDateTimeOriginal() {
		return dateTimeOriginal;
	}

	public void setDateTimeOriginal(Date dateTimeOriginal) {
		this.dateTimeOriginal = dateTimeOriginal;
	}
}
