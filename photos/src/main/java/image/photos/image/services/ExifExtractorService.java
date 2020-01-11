package image.photos.image.services;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.*;
import com.drew.metadata.jpeg.JpegDescriptor;
import com.drew.metadata.jpeg.JpegDirectory;
import image.persistence.entity.image.ExifData;
import image.persistence.entity.image.ImageMetadata;
import image.photos.image.helpers.ThumbHelper;
import image.photos.infrastructure.filestore.FileStoreService;
import image.photos.util.process.ProcessRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import static exifweb.util.SuppressExceptionUtils.ignoreExc;
import static image.persistence.entity.util.DateUtils.safeParse;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
@Slf4j
public class ExifExtractorService {
	/**
	 * metadata extractor uses this yyyy.MM.dd format
	 */
	private static final DateTimeFormatter sdf =
			DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss").withZone(ZoneOffset.UTC);
	private static final int WIDTH = 0;
	private static final int HEIGHT = 1;
	private final int maxThumbSizeInt;
	private final ProcessRunner processRunner;
	private final ThumbHelper thumbHelper;
	private final FileStoreService fileStoreService;

	public ExifExtractorService(@Value("${max.thumb.size}") int maxThumbSizeInt,
			ProcessRunner processRunner, ThumbHelper thumbHelper,
			FileStoreService fileStoreService) {
		this.maxThumbSizeInt = maxThumbSizeInt;
		this.processRunner = processRunner;
		this.thumbHelper = thumbHelper;
		this.fileStoreService = fileStoreService;
	}

	public ImageMetadata extractMetadata(Path imgFile) throws IOException {
		// ImageMetadata
		ImageMetadata imageMetadata = new ImageMetadata(new Date(
				this.fileStoreService.lastModifiedTime(imgFile)));

		// EXIF loading
		try {
			this.loadExifFromImgFile(imageMetadata.getExifData(), imgFile);
		} catch (FileNotFoundException | NoSuchFileException e) {
			// path no longer exists
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("EXIF error:\n{}", imgFile);
		}

		// when null than set exifData.datetimeOriginal to Image's dateTime
		if (imageMetadata.getExifData().getDateTimeOriginal() == null) {
			imageMetadata.getExifData().setDateTimeOriginal(imageMetadata.getDateTime());
		}

		// update thumb last modified date
		Date thumbLastModified = this.thumbHelper.thumbLastModified(
				imgFile, imageMetadata.getDateTime());
		imageMetadata.setThumbLastModified(thumbLastModified);

		// update exifData with the Image's dimensions
		if (imageMetadata.getExifData().getImageHeight() == 0 ||
				imageMetadata.getExifData().getImageWidth() == 0) {
			this.loadDimensions(imageMetadata.getExifData(), imgFile);
		}

		return imageMetadata;
	}

	/**
	 * https://github.com/drewnoakes/metadata-extractor/wiki/SampleOutput
	 */
	private void loadExifFromImgFile(ExifData exifData, Path imgFile) throws ImageProcessingException, IOException {
		Metadata metadata = ImageMetadataReader.readMetadata(imgFile.toFile());
		Directory directory = metadata.getFirstDirectoryOfType(JpegDirectory.class);

		JpegDescriptor jpegDescriptor = new JpegDescriptor((JpegDirectory) directory);
		exifData.setImageHeight(Integer.parseInt(jpegDescriptor.getImageHeightDescription().replace(" pixels", "")));
		exifData.setImageWidth(Integer.parseInt(jpegDescriptor.getImageWidthDescription().replace(" pixels", "")));

		directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
		ExifSubIFDDescriptor exifSubIFDDescriptor = new ExifSubIFDDescriptor((ExifSubIFDDirectory) directory);
		List<Exception> exifErrors = new ArrayList<>();
		Consumer<Runnable> ignoreExcWithLog = r -> ignoreExc(r, exifErrors::add);
		ignoreExcWithLog.accept(() -> exifData.setExposureTime(exifSubIFDDescriptor.getExposureTimeDescription()));
		ignoreExcWithLog.accept(() -> exifData.setfNumber(exifSubIFDDescriptor.getFNumberDescription()));
		ignoreExcWithLog.accept(() -> exifData.setExposureProgram(exifSubIFDDescriptor.getExposureProgramDescription()));
		ignoreExcWithLog.accept(() -> exifData.setIsoSpeedRatings(
				Integer.parseInt(exifSubIFDDescriptor.getIsoEquivalentDescription())));
		ignoreExcWithLog.accept(() -> exifData.setDateTimeOriginal(
				safeParse(exifSubIFDDescriptor
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

		if (!exifErrors.isEmpty()) {
			log.error("EXIF errors ({}) exist for:\n{}", exifErrors.size(), imgFile);
		}
	}

	private void loadDimensions(ExifData imageDimensions, Path path) {
		try {
			ProcessBuilder identifyImgDimensions = new ProcessBuilder(
					"identify", "-format", "%[fx:w] %[fx:h]", path.toString());
			String sDimensions = this.processRunner.getProcessOutput(identifyImgDimensions);
			String[] dims = sDimensions.split("\\s");
			imageDimensions.setImageWidth(Integer.parseInt(dims[WIDTH]));
			imageDimensions.setImageHeight(Integer.parseInt(dims[HEIGHT]));
		} catch (Exception e) {
			log.error("Using default dimensions ({}x{}) for:\n{}",
					this.maxThumbSizeInt, this.maxThumbSizeInt, path);
			imageDimensions.setImageWidth(this.maxThumbSizeInt);
			imageDimensions.setImageHeight(this.maxThumbSizeInt);
		}
	}
}
