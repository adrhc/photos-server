package image.photos.image;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.*;
import com.drew.metadata.jpeg.JpegDescriptor;
import com.drew.metadata.jpeg.JpegDirectory;
import exifweb.util.MiscUtils;
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
		ignoreExc(() -> exifData.setExposureTime(exifSubIFDDescriptor.getExposureTimeDescription()));
		ignoreExc(() -> exifData.setfNumber(exifSubIFDDescriptor.getFNumberDescription()));
		ignoreExc(() -> exifData.setExposureProgram(exifSubIFDDescriptor.getExposureProgramDescription()));
		ignoreExc(() -> exifData.setIsoSpeedRatings(
				Integer.parseInt(exifSubIFDDescriptor.getIsoEquivalentDescription())));
		ignoreExc(() -> exifData.setDateTimeOriginal(ignoreExc(exifSubIFDDescriptor
				.getDescription(ExifDirectoryBase.TAG_DATETIME_ORIGINAL), sdf)));
		ignoreExc(() -> exifData.setShutterSpeedValue(exifSubIFDDescriptor.getShutterSpeedDescription()));
		ignoreExc(() -> exifData.setApertureValue(exifSubIFDDescriptor.getApertureValueDescription()));
		ignoreExc(() -> exifData.setExposureBiasValue(exifSubIFDDescriptor.getExposureBiasDescription()));
		ignoreExc(() -> exifData.setMeteringMode(exifSubIFDDescriptor.getMeteringModeDescription()));
		ignoreExc(() -> exifData.setFlash(exifSubIFDDescriptor.getFlashDescription()));
		ignoreExc(() -> exifData.setFocalLength(exifSubIFDDescriptor.getFocalLengthDescription()));
		ignoreExc(() -> exifData.setExposureMode(exifSubIFDDescriptor.getExposureModeDescription()));
		ignoreExc(() -> exifData.setWhiteBalanceMode(exifSubIFDDescriptor.getWhiteBalanceModeDescription()));
		ignoreExc(() -> exifData.setSceneCaptureType(exifSubIFDDescriptor.getSceneCaptureTypeDescription()));
		ignoreExc(() -> exifData.setGainControl(exifSubIFDDescriptor.getGainControlDescription()));
		ignoreExc(() -> exifData.setContrast(exifSubIFDDescriptor.getContrastDescription()));
		ignoreExc(() -> exifData.setSaturation(exifSubIFDDescriptor.getSaturationDescription()));
		ignoreExc(() -> exifData.setSharpness(exifSubIFDDescriptor.getSharpnessDescription()));
		ignoreExc(() -> exifData.setSubjectDistanceRange(exifSubIFDDescriptor
				.getSubjectDistanceRangeDescription()));
		ignoreExc(() -> exifData.setLensModel(exifSubIFDDescriptor
				.getDescription(ExifDirectoryBase.TAG_LENS_MODEL)));

		directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
		ExifIFD0Descriptor exifIFD0Descriptor = new ExifIFD0Descriptor((ExifIFD0Directory) directory);
		ignoreExc(() -> exifData.setModel(exifIFD0Descriptor.getDescription(ExifDirectoryBase.TAG_MODEL)));
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
