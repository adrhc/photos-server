package image.exifweb.image.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import image.exifweb.system.persistence.entities.image.ExifData;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by adr on 2/10/18.
 */
public class ExifInfo extends ExifData {
	private Integer id;
	private String name;
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
}
