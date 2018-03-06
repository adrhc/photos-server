package image.persistence.entity;

import image.cdm.image.ExifInfo;
import image.persistence.util.IPositiveRandom;

import java.util.Date;

public interface IExifInfoSupplier extends IPositiveRandom {
	default ExifInfo supplyExifInfo() {
		int random = positiveRandom();
		ExifInfo exifInfo = new ExifInfo();
		exifInfo.setApertureValue("apertureValue-" + random);
		exifInfo.setContrast("contrast-" + random);
		exifInfo.setDateTime(new Date());
		exifInfo.setDateTimeOriginal(new Date());
		exifInfo.setImageHeight(random);
		exifInfo.setImageWidth(random + 1);
		return exifInfo;
	}
}
