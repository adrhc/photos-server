package image.cdm.image;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by adr on 2/10/18.
 */
public class ExifInfo implements Serializable {
	private Integer id;
	private String name;
	/**
	 * used in picture's url (impact browser-cache)
	 */
	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
	private Date dateTime;
	/**
	 * related to image file change
	 * used in thumb's url (impact browser-cache)
	 */
	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
	private Date thumbLastModified;
	private int imageHeight;
	private int imageWidth;
	private String apertureValue;
	private String contrast;
	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
	private Date dateTimeOriginal;
	private String lensModel;
	private String meteringMode;
	private String model;
	private String saturation;
	private String sceneCaptureType;
	private String sharpness;
	private String shutterSpeedValue;
	private String subjectDistanceRange;
	private String whiteBalanceMode;
	private String exposureBiasValue;
	private String exposureMode;
	private String exposureProgram;
	private String exposureTime;
	private String fNumber;
	private String flash;
	private String focalLength;
	private String gainControl;
	private int isoSpeedRatings;

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

	public String getfNumber() {
		return fNumber;
	}

	public void setfNumber(String fNumber) {
		this.fNumber = fNumber;
	}

	public String getFlash() {
		return flash;
	}

	public void setFlash(String flash) {
		this.flash = flash;
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

	public int getIsoSpeedRatings() {
		return isoSpeedRatings;
	}

	public void setIsoSpeedRatings(int isoSpeedRatings) {
		this.isoSpeedRatings = isoSpeedRatings;
	}
}
