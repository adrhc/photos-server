package image.photos.image;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Descriptor;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDescriptor;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.jpeg.JpegDescriptor;
import com.drew.metadata.jpeg.JpegDirectory;
import image.persistence.entity.image.ExifData;
import image.persistence.entity.image.ImageMetadata;
import image.photos.util.process.ProcessRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ExifExtractorService {
	private static final Logger logger = LoggerFactory.getLogger(ExifExtractorService.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
	private static final int WIDTH = 0;
	private static final int HEIGHT = 1;
	@Value("${max.thumb.size}")
	private int maxThumbSizeInt;
	@Inject
	private ProcessRunner processRunner;
	@Inject
	private ThumbUtils thumbUtils;

	public ImageMetadata extractMetadata(File imgFile) {
		ImageMetadata imageMetadata =
				new ImageMetadata(new Date(imgFile.lastModified()));

		try {
			loadExifFromImgFile(imageMetadata.getExifData(), imgFile);
		} catch (FileNotFoundException e) {
			// path no longer exists
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error("{}: {}", imgFile.getParentFile().getName(), imgFile.getName());
		}

		if (imageMetadata.getExifData().getDateTimeOriginal() == null) {
			imageMetadata.getExifData().setDateTimeOriginal(imageMetadata.getDateTime());
		}
		if (imageMetadata.getExifData().getImageHeight() == 0 ||
				imageMetadata.getExifData().getImageWidth() == 0) {
			loadDimensions(imageMetadata.getExifData(), imgFile.getPath());
		}

		Date thumbLastModified = thumbUtils.getThumbLastModified(
				imgFile, imageMetadata.getDateTime());
		imageMetadata.setThumbLastModified(thumbLastModified);

		return imageMetadata;
	}

	/**
	 * https://github.com/drewnoakes/metadata-extractor/wiki/SampleOutput
	 *
	 * @param exifData
	 * @param imgFile
	 * @throws Exception
	 */
	private void loadExifFromImgFile(ExifData exifData, File imgFile) throws Exception {
		Metadata metadata = ImageMetadataReader.readMetadata(imgFile);
		Directory directory = metadata.getFirstDirectoryOfType(JpegDirectory.class);

		JpegDescriptor jpegDescriptor = new JpegDescriptor((JpegDirectory) directory);
		exifData.setImageHeight(Integer.parseInt(jpegDescriptor.getImageHeightDescription().replace(" pixels", "")));
		exifData.setImageWidth(Integer.parseInt(jpegDescriptor.getImageWidthDescription().replace(" pixels", "")));

		directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
		ExifSubIFDDescriptor exifSubIFDDescriptor = new ExifSubIFDDescriptor((ExifSubIFDDirectory) directory);
		safeCall(() -> exifData.setExposureTime(exifSubIFDDescriptor.getExposureTimeDescription()));
		safeCall(() -> exifData.setfNumber(exifSubIFDDescriptor.getFNumberDescription()));
		safeCall(() -> exifData.setExposureProgram(exifSubIFDDescriptor.getExposureProgramDescription()));
		safeCall(() -> exifData.setIsoSpeedRatings(
				Integer.parseInt(exifSubIFDDescriptor.getIsoEquivalentDescription())));
		safeCall(() -> exifData.setDateTimeOriginal(safeDateParse(exifSubIFDDescriptor
				.getDescription(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL))));
		safeCall(() -> exifData.setShutterSpeedValue(exifSubIFDDescriptor.getShutterSpeedDescription()));
		safeCall(() -> exifData.setApertureValue(exifSubIFDDescriptor.getApertureValueDescription()));
		safeCall(() -> exifData.setExposureBiasValue(exifSubIFDDescriptor.getExposureBiasDescription()));
		safeCall(() -> exifData.setMeteringMode(exifSubIFDDescriptor.getMeteringModeDescription()));
		safeCall(() -> exifData.setFlash(exifSubIFDDescriptor.getFlashDescription()));
		safeCall(() -> exifData.setFocalLength(exifSubIFDDescriptor.getFocalLengthDescription()));
		safeCall(() -> exifData.setExposureMode(exifSubIFDDescriptor.getExposureModeDescription()));
		safeCall(() -> exifData.setWhiteBalanceMode(exifSubIFDDescriptor.getWhiteBalanceModeDescription()));
		safeCall(() -> exifData.setSceneCaptureType(exifSubIFDDescriptor.getSceneCaptureTypeDescription()));
		safeCall(() -> exifData.setGainControl(exifSubIFDDescriptor.getGainControlDescription()));
		safeCall(() -> exifData.setContrast(exifSubIFDDescriptor.getContrastDescription()));
		safeCall(() -> exifData.setSaturation(exifSubIFDDescriptor.getSaturationDescription()));
		safeCall(() -> exifData.setSharpness(exifSubIFDDescriptor.getSharpnessDescription()));
		safeCall(() -> exifData.setSubjectDistanceRange(exifSubIFDDescriptor
				.getSubjectDistanceRangeDescription()));
		safeCall(() -> exifData.setLensModel(exifSubIFDDescriptor
				.getDescription(ExifSubIFDDirectory.TAG_LENS_MODEL)));

		directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
		ExifIFD0Descriptor exifIFD0Descriptor = new ExifIFD0Descriptor((ExifIFD0Directory) directory);
		safeCall(() -> exifData.setModel(exifIFD0Descriptor.getDescription(ExifIFD0Directory.TAG_MODEL)));
	}

	private Date safeDateParse(String s) {
		try {
			return sdf.parse(s);
		} catch (Exception e) {
			return null;
		}
	}

	private void safeCall(Runnable c) {
		try {
			c.run();
		} catch (Exception e) {
			logger.trace(e.getMessage(), e);
		}
	}

	private void loadDimensions(ExifData imageDimensions, String path) {
		try {
//			ProcessBuilder identifyImgDimensions = new ProcessBuilder(
//					"/home/adr/x.sh", "image_dims", path);
			ProcessBuilder identifyImgDimensions = new ProcessBuilder(
					"identify", "-format", "%[fx:w] %[fx:h]", path);
			String sDimensions = processRunner.getProcessOutput(identifyImgDimensions);
//            logger.debug("dimensions {} for:\n{}", dimensions, path);
			String[] dims = sDimensions.split("\\s");
			imageDimensions.setImageWidth(Integer.parseInt(dims[WIDTH]));
			imageDimensions.setImageHeight(Integer.parseInt(dims[HEIGHT]));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error("Using default dimensions: {}x{}", maxThumbSizeInt, maxThumbSizeInt);
			imageDimensions.setImageWidth(maxThumbSizeInt);
			imageDimensions.setImageHeight(maxThumbSizeInt);
		}
	}
}
