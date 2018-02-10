package image.exifweb.album.importer;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Descriptor;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDescriptor;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.jpeg.JpegDescriptor;
import com.drew.metadata.jpeg.JpegDirectory;
import image.exifweb.image.IImageDimensions;
import image.exifweb.util.procinfo.ProcessInfoService;
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
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");
	private static final int WIDTH = 0;
	private static final int HEIGHT = 1;
	@Value("${max.thumb.size}")
	private int maxThumbSizeInt;
	@Inject
	private ProcessInfoService processInfoService;
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
			logger.error("{}: {}", imgFile.getParentFile().getName(), imgFile.getName());
			imageMetadata.getExifData().setDateTimeOriginal(imageMetadata.getDateTime());
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
		Directory directory = metadata.getDirectory(JpegDirectory.class);

		JpegDescriptor jpegDescriptor = new JpegDescriptor((JpegDirectory) directory);
		exifData.setImageHeight(Integer.parseInt(jpegDescriptor.getImageHeightDescription().replace(" pixels", "")));
		exifData.setImageWidth(Integer.parseInt(jpegDescriptor.getImageWidthDescription().replace(" pixels", "")));

		directory = metadata.getDirectory(ExifSubIFDDirectory.class);
		ExifSubIFDDescriptor exifSubIFDDescriptor = new ExifSubIFDDescriptor((ExifSubIFDDirectory) directory);
		exifData.setExposureTime(exifSubIFDDescriptor.getExposureTimeDescription());
		exifData.setfNumber(exifSubIFDDescriptor.getFNumberDescription());
		exifData.setExposureProgram(exifSubIFDDescriptor.getExposureProgramDescription());
		exifData.setIsoSpeedRatings(Integer.parseInt(exifSubIFDDescriptor.getIsoEquivalentDescription()));
		exifData.setDateTimeOriginal(sdf.parse(
				exifSubIFDDescriptor.getDescription(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)));
		exifData.setShutterSpeedValue(exifSubIFDDescriptor.getShutterSpeedDescription());
		exifData.setApertureValue(exifSubIFDDescriptor.getApertureValueDescription());
		exifData.setExposureBiasValue(exifSubIFDDescriptor.getExposureBiasDescription());
		exifData.setMeteringMode(exifSubIFDDescriptor.getMeteringModeDescription());
		exifData.setFlash(exifSubIFDDescriptor.getFlashDescription());
		exifData.setFocalLength(exifSubIFDDescriptor.getFocalLengthDescription());
		exifData.setExposureMode(exifSubIFDDescriptor.getExposureModeDescription());
		exifData.setWhiteBalanceMode(exifSubIFDDescriptor.getWhiteBalanceModeDescription());
		exifData.setSceneCaptureType(exifSubIFDDescriptor.getSceneCaptureTypeDescription());
		exifData.setGainControl(exifSubIFDDescriptor.getGainControlDescription());
		exifData.setContrast(exifSubIFDDescriptor.getContrastDescription());
		exifData.setSaturation(exifSubIFDDescriptor.getSaturationDescription());
		exifData.setSharpness(exifSubIFDDescriptor.getSharpnessDescription());
		exifData.setSubjectDistanceRange(exifSubIFDDescriptor.getSubjectDistanceRangeDescription());
		exifData.setLensModel(exifSubIFDDescriptor.getDescription(ExifSubIFDDirectory.TAG_LENS_MODEL));

		directory = metadata.getDirectory(ExifIFD0Directory.class);
		ExifIFD0Descriptor exifIFD0Descriptor = new ExifIFD0Descriptor((ExifIFD0Directory) directory);
		exifData.setModel(exifIFD0Descriptor.getDescription(ExifIFD0Directory.TAG_MODEL));
	}

	private void loadDimensions(IImageDimensions imageDimensions, String path) {
		try {
//			ProcessBuilder identifyImgDimensions = new ProcessBuilder(
//					"/home/adr/x.sh", "image_dims", path);
			ProcessBuilder identifyImgDimensions = new ProcessBuilder(
					"identify", "-format", "%[fx:w] %[fx:h]", path);
			String sDimensions = processInfoService.getProcessOutput(identifyImgDimensions);
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
