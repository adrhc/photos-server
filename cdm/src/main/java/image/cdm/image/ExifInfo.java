package image.cdm.image;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by adr on 2/10/18.
 */
public class ExifInfo implements Serializable {
	//	public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
	private Integer id;
	private String name;
	/**
	 * related to image's file change
	 * used for image's url (impact browser-cache)
	 */
//	@JsonFormat(pattern = DATE_FORMAT)
	private Date dateTime;
	private int imageHeight;
	private int imageWidth;
	private String apertureValue;
	private String contrast;
	//	@JsonFormat(pattern = DATE_FORMAT)
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
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateTime() {
		return this.dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public Date getDateTimeOriginal() {
		return this.dateTimeOriginal;
	}

	public void setDateTimeOriginal(Date dateTimeOriginal) {
		this.dateTimeOriginal = dateTimeOriginal;
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

	public String getfNumber() {
		return this.fNumber;
	}

	public void setfNumber(String fNumber) {
		this.fNumber = fNumber;
	}

	public String getFlash() {
		return this.flash;
	}

	public void setFlash(String flash) {
		this.flash = flash;
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

	@Override
	public String toString() {
		return "ExifInfo{" +
				"id=" + this.id +
				", name='" + this.name + '\'' +
				", dateTime=" + this.dateTime +
				", imageHeight=" + this.imageHeight +
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
