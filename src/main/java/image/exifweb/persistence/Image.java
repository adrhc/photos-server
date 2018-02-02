package image.exifweb.persistence;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import image.exifweb.image.ImageDimensions;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Watch out to triggers!
 * e.g. ALBUM_DIRTY_ON_RATING used when using nginx drizzle (mysql)
 * <p>
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/7/13
 * Time: 9:06 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, scope = Image.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Image implements ImageDimensions, Serializable {
	public static final Byte DEFAULT_STATUS = 0;
	public static final Byte DEFAULT_RATING = 1;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false, length = 256)
	private String name;
	/**
	 * see DEFAULT_STATUS = 0 defined above
	 */
	@Column(nullable = false)
	private Byte status = DEFAULT_STATUS;
	@Column(nullable = false)
	private boolean deleted;
	@Formula("status & 1")
	private boolean hidden;
	@Formula("status & 2")
	private boolean personal;
	@Formula("status & 4")
	private boolean ugly;
	@Formula("status & 8")
	private boolean duplicate;
	@Column
	private String apertureValue;
	@Column
	private String contrast;
	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Bucharest")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date dateTime;
	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Bucharest")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date dateTimeOriginal;
	/**
	 * related to image file change
	 * used for thumb's url (impact browser-cache)
	 */
	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Bucharest")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "thumb_last_modified", nullable = false)
	private Date thumbLastModified;
	@Column
	private String exposureBiasValue;
	@Column
	private String exposureMode;
	@Column
	private String exposureProgram;
	@Column
	private String exposureTime;
	@Column
	private String fNumber;
	@Column
	private String flash;
	@Column
	private String focalLength;
	@Column
	private String gainControl;
	@Column
	private int isoSpeedRatings;
	@Column(nullable = false)
	private int imageHeight;
	@Column(nullable = false)
	private int imageWidth;
	@Column
	private String lensModel;
	@Column
	private String meteringMode;
	@Column
	private String model;
	@Column
	private String saturation;
	@Column
	private String sceneCaptureType;
	@Column
	private String sharpness;
	@Column
	private String shutterSpeedValue;
	@Column
	private String subjectDistanceRange;
	@Column
	private String whiteBalanceMode;
	/**
	 * see DEFAULT_RATING = 1 defined above
	 */
	@Column(nullable = false, columnDefinition = "INTEGER(1) NOT NULL DEFAULT 1")
	private byte rating = DEFAULT_RATING;
//	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_ALBUM")
	private Album album;
	/*
	 * related to db record -> rating, status, deleted change
	 */
	@Version
	@Column(name = "last_update", nullable = false)
	private Timestamp lastUpdate;

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

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

	public String getApertureValue() {
		return apertureValue;
	}

	public void setApertureValue(String apertureValue) {
		this.apertureValue = apertureValue;
	}

	public String getContrast() {
		return contrast;
	}

	public void setContrast(String contrast) {
		this.contrast = contrast;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public Date getDateTimeOriginal() {
		return dateTimeOriginal;
	}

	public void setDateTimeOriginal(Date dateTimeOriginal) {
		this.dateTimeOriginal = dateTimeOriginal;
	}

	public String getExposureBiasValue() {
		return exposureBiasValue;
	}

	public void setExposureBiasValue(String exposureBiasValue) {
		this.exposureBiasValue = exposureBiasValue;
	}

	public String getExposureMode() {
		return exposureMode;
	}

	public void setExposureMode(String exposureMode) {
		this.exposureMode = exposureMode;
	}

	public String getExposureProgram() {
		return exposureProgram;
	}

	public void setExposureProgram(String exposureProgram) {
		this.exposureProgram = exposureProgram;
	}

	public String getExposureTime() {
		return exposureTime;
	}

	public void setExposureTime(String exposureTime) {
		this.exposureTime = exposureTime;
	}

	public String getFlash() {
		return flash;
	}

	public void setFlash(String flash) {
		this.flash = flash;
	}

	public String getfNumber() {
		return fNumber;
	}

	public void setfNumber(String fNumber) {
		this.fNumber = fNumber;
	}

	public String getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(String focalLength) {
		this.focalLength = focalLength;
	}

	public String getGainControl() {
		return gainControl;
	}

	public void setGainControl(String gainControl) {
		this.gainControl = gainControl;
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

	public int getIsoSpeedRatings() {
		return isoSpeedRatings;
	}

	public void setIsoSpeedRatings(int isoSpeedRatings) {
		this.isoSpeedRatings = isoSpeedRatings;
	}

	public String getLensModel() {
		return lensModel;
	}

	public void setLensModel(String lensModel) {
		this.lensModel = lensModel;
	}

	public String getMeteringMode() {
		return meteringMode;
	}

	public void setMeteringMode(String meteringMode) {
		this.meteringMode = meteringMode;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSaturation() {
		return saturation;
	}

	public void setSaturation(String saturation) {
		this.saturation = saturation;
	}

	public String getSceneCaptureType() {
		return sceneCaptureType;
	}

	public void setSceneCaptureType(String sceneCaptureType) {
		this.sceneCaptureType = sceneCaptureType;
	}

	public String getSharpness() {
		return sharpness;
	}

	public void setSharpness(String sharpness) {
		this.sharpness = sharpness;
	}

	public String getShutterSpeedValue() {
		return shutterSpeedValue;
	}

	public void setShutterSpeedValue(String shutterSpeedValue) {
		this.shutterSpeedValue = shutterSpeedValue;
	}

	public String getSubjectDistanceRange() {
		return subjectDistanceRange;
	}

	public void setSubjectDistanceRange(String subjectDistanceRange) {
		this.subjectDistanceRange = subjectDistanceRange;
	}

	public String getWhiteBalanceMode() {
		return whiteBalanceMode;
	}

	public void setWhiteBalanceMode(String whiteBalanceMode) {
		this.whiteBalanceMode = whiteBalanceMode;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public boolean isHidden() {
		return hidden;
	}

	public boolean isPersonal() {
		return personal;
	}

	public boolean isUgly() {
		return ugly;
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getThumbLastModified() {
		return thumbLastModified;
	}

	public void setThumbLastModified(Date thumbLastModified) {
		this.thumbLastModified = thumbLastModified;
	}

	public byte getRating() {
		return rating;
	}

	public void setRating(byte rating) {
		this.rating = rating;
	}
}
