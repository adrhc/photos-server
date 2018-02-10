package image.exifweb.album.importer;

import com.fasterxml.jackson.annotation.JsonFormat;
import image.exifweb.image.IImageDimensions;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by adr on 2/10/18.
 */
@Embeddable
public class ExifData implements IImageDimensions, Serializable {
	@Column(nullable = false)
	private int imageHeight;
	@Column(nullable = false)
	private int imageWidth;
	@Column
	private String apertureValue;
	@Column
	private String contrast;
	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date dateTimeOriginal;
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
}
