package image.photos.image;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.*;
import com.drew.metadata.jpeg.JpegDescriptor;
import com.drew.metadata.jpeg.JpegDirectory;
import exifweb.util.MiscUtils;
import image.persistence.entitytests.image.ExifData;
import image.persistence.entitytests.image.ImageMetadata;
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
import java.util.function.Consumer;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ExifExtractorService implements MiscUtils {
	private static final Logger logger = LoggerFactory.getLogger(ExifExtractorService.class);
	/**
	 * metadata extractor uses this yyyy:MM:dd format
	 */
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

		Date thumbLastModified = this.thumbUtils.getThumbLastModified(
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
		Consumer<Runnable> ignoreExcWithLog = r -> ignoreExc(r, e -> logger.error("EXIF error: {}", imgFile.getPath()));
		ignoreExcWithLog.accept(() -> exifData.setExposureTime(exifSubIFDDescriptor.getExposureTimeDescription()));
		ignoreExcWithLog.accept(() -> exifData.setfNumber(exifSubIFDDescriptor.getFNumberDescription()));
		ignoreExcWithLog.accept(() -> exifData.setExposureProgram(exifSubIFDDescriptor.getExposureProgramDescription()));
		ignoreExcWithLog.accept(() -> exifData.setIsoSpeedRatings(
				Integer.parseInt(exifSubIFDDescriptor.getIsoEquivalentDescription())));
		ignoreExcWithLog.accept(() -> exifData.setDateTimeOriginal(safeDateParse(exifSubIFDDescriptor
				.getDescription(ExifDirectoryBase.TAG_DATETIME_ORIGINAL), sdf)));
		ignoreExcWithLog.accept(() -> exifData.setShutterSpeedValue(exifSubIFDDescriptor.getShutterSpeedDescription()));
		ignoreExcWithLog.accept(() -> exifData.setApertureValue(exifSubIFDDescriptor.getApertureValueDescription()));
		ignoreExcWithLog.accept(() -> exifData.setExposureBiasValue(exifSubIFDDescriptor.getExposureBiasDescription()));
		ignoreExcWithLog.accept(() -> exifData.setMeteringMode(exifSubIFDDescriptor.getMeteringModeDescription()));
		ignoreExcWithLog.accept(() -> exifData.setFlash(exifSubIFDDescriptor.getFlashDescription()));
		ignoreExcWithLog.accept(() -> exifData.setFocalLength(exifSubIFDDescriptor.getFocalLengthDescription()));
		ignoreExcWithLog.accept(() -> exifData.setExposureMode(exifSubIFDDescriptor.getExposureModeDescription()));
		ignoreExcWithLog.accept(() -> exifData.setWhiteBalanceMode(exifSubIFDDescriptor.getWhiteBalanceModeDescription()));
		ignoreExcWithLog.accept(() -> exifData.setSceneCaptureType(exifSubIFDDescriptor.getSceneCaptureTypeDescription()));
		ignoreExcWithLog.accept(() -> exifData.setGainControl(exifSubIFDDescriptor.getGainControlDescription()));
		ignoreExcWithLog.accept(() -> exifData.setContrast(exifSubIFDDescriptor.getContrastDescription()));
		ignoreExcWithLog.accept(() -> exifData.setSaturation(exifSubIFDDescriptor.getSaturationDescription()));
		ignoreExcWithLog.accept(() -> exifData.setSharpness(exifSubIFDDescriptor.getSharpnessDescription()));
		ignoreExcWithLog.accept(() -> exifData.setSubjectDistanceRange(exifSubIFDDescriptor
				.getSubjectDistanceRangeDescription()));
		ignoreExcWithLog.accept(() -> exifData.setLensModel(exifSubIFDDescriptor
				.getDescription(ExifDirectoryBase.TAG_LENS_MODEL)));

		directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
		ExifIFD0Descriptor exifIFD0Descriptor = new ExifIFD0Descriptor((ExifIFD0Directory) directory);
		ignoreExcWithLog.accept(() -> exifData.setModel(exifIFD0Descriptor.getDescription(ExifDirectoryBase.TAG_MODEL)));
	}

	private void loadDimensions(ExifData imageDimensions, String path) {
		try {
//			ProcessBuilder identifyImgDimensions = new ProcessBuilder(
//					"/home/adr/x.sh", "image_dims", path);
			ProcessBuilder identifyImgDimensions = new ProcessBuilder(
					"identify", "-format", "%[fx:w] %[fx:h]", path);
			String sDimensions = this.processRunner.getProcessOutput(identifyImgDimensions);
//            logger.debug("dimensions {} for:\n{}", dimensions, path);
			String[] dims = sDimensions.split("\\s");
			imageDimensions.setImageWidth(Integer.parseInt(dims[WIDTH]));
			imageDimensions.setImageHeight(Integer.parseInt(dims[HEIGHT]));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error("Using default dimensions: {}x{}", this.maxThumbSizeInt, this.maxThumbSizeInt);
			imageDimensions.setImageWidth(this.maxThumbSizeInt);
			imageDimensions.setImageHeight(this.maxThumbSizeInt);
		}
	}
}
