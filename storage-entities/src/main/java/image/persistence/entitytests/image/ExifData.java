package image.persistence.entitytests.image;

import image.persistence.entity.IStorageEntity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by adr on 2/10/18.
 */
@Embeddable
public class ExifData implements IStorageEntity {
	@Column(nullable = false)
	private int imageHeight;
	@Column(nullable = false)
	private int imageWidth;
	@Column
	private String apertureValue;
	@Column
	private String contrast;
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
		return this.apertureValue;
	}

	public void setApertureValue(String apertureValue) {
		this.apertureValue = apertureValue;
	}

	public String getContrast() {
		return this.contrast;
	}

	public void setContrast(String contrast) {
		this.contrast = contrast;
	}

	public Date getDateTimeOriginal() {
		return this.dateTimeOriginal;
	}

	public void setDateTimeOriginal(Date dateTimeOriginal) {
		this.dateTimeOriginal = dateTimeOriginal;
	}

	public String getExposureBiasValue() {
		return this.exposureBiasValue;
	}

	public void setExposureBiasValue(String exposureBiasValue) {
		this.exposureBiasValue = exposureBiasValue;
	}

	public String getExposureMode() {
		return this.exposureMode;
	}

	public void setExposureMode(String exposureMode) {
		this.exposureMode = exposureMode;
	}

	public String getExposureProgram() {
		return this.exposureProgram;
	}

	public void setExposureProgram(String exposureProgram) {
		this.exposureProgram = exposureProgram;
	}

	public String getExposureTime() {
		return this.exposureTime;
	}

	public void setExposureTime(String exposureTime) {
		this.exposureTime = exposureTime;
	}

	public String getFlash() {
		return this.flash;
	}

	public void setFlash(String flash) {
		this.flash = flash;
	}

	public String getfNumber() {
		return this.fNumber;
	}

	public void setfNumber(String fNumber) {
		this.fNumber = fNumber;
	}

	public String getFocalLength() {
		return this.focalLength;
	}

	public void setFocalLength(String focalLength) {
		this.focalLength = focalLength;
	}

	public String getGainControl() {
		return this.gainControl;
	}

	public void setGainControl(String gainControl) {
		this.gainControl = gainControl;
	}

	public int getIsoSpeedRatings() {
		return this.isoSpeedRatings;
	}

	public void setIsoSpeedRatings(int isoSpeedRatings) {
		this.isoSpeedRatings = isoSpeedRatings;
	}

	public String getLensModel() {
		return this.lensModel;
	}

	public void setLensModel(String lensModel) {
		this.lensModel = lensModel;
	}

	public String getMeteringMode() {
		return this.meteringMode;
	}

	public void setMeteringMode(String meteringMode) {
		this.meteringMode = meteringMode;
	}

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSaturation() {
		return this.saturation;
	}

	public void setSaturation(String saturation) {
		this.saturation = saturation;
	}

	public String getSceneCaptureType() {
		return this.sceneCaptureType;
	}

	public void setSceneCaptureType(String sceneCaptureType) {
		this.sceneCaptureType = sceneCaptureType;
	}

	public String getSharpness() {
		return this.sharpness;
	}

	public void setSharpness(String sharpness) {
		this.sharpness = sharpness;
	}

	public String getShutterSpeedValue() {
		return this.shutterSpeedValue;
	}

	public void setShutterSpeedValue(String shutterSpeedValue) {
		this.shutterSpeedValue = shutterSpeedValue;
	}

	public String getSubjectDistanceRange() {
		return this.subjectDistanceRange;
	}

	public void setSubjectDistanceRange(String subjectDistanceRange) {
		this.subjectDistanceRange = subjectDistanceRange;
	}

	public String getWhiteBalanceMode() {
		return this.whiteBalanceMode;
	}

	public void setWhiteBalanceMode(String whiteBalanceMode) {
		this.whiteBalanceMode = whiteBalanceMode;
	}

	public int getImageHeight() {
		return this.imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public int getImageWidth() {
		return this.imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	@Override
	public String toString() {
		return "ExifData{" +
				"imageHeight=" + this.imageHeight +
				", imageWidth=" + this.imageWidth +
				", apertureValue='" + this.apertureValue + '\'' +
				", contrast='" + this.contrast + '\'' +
				", dateTimeOriginal=" + this.dateTimeOriginal +
				", lensModel='" + this.lensModel + '\'' +
				", meteringMode='" + this.meteringMode + '\'' +
				", model='" + this.model + '\'' +
				", saturation='" + this.saturation + '\'' +
				", sceneCaptureType='" + this.sceneCaptureType + '\'' +
				", sharpness='" + this.sharpness + '\'' +
				", shutterSpeedValue='" + this.shutterSpeedValue + '\'' +
				", subjectDistanceRange='" + this.subjectDistanceRange + '\'' +
				", whiteBalanceMode='" + this.whiteBalanceMode + '\'' +
				", exposureBiasValue='" + this.exposureBiasValue + '\'' +
				", exposureMode='" + this.exposureMode + '\'' +
				", exposureProgram='" + this.exposureProgram + '\'' +
				", exposureTime='" + this.exposureTime + '\'' +
				", fNumber='" + this.fNumber + '\'' +
				", flash='" + this.flash + '\'' +
				", focalLength='" + this.focalLength + '\'' +
				", gainControl='" + this.gainControl + '\'' +
				", isoSpeedRatings=" + this.isoSpeedRatings +
				'}';
	}
}
