package image.exifweb.action;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Descriptor;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDescriptor;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.jpeg.JpegDescriptor;
import com.drew.metadata.jpeg.JpegDirectory;
import image.exifweb.persistence.Image;
import image.exifweb.sys.ProcessInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
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
public class ImageExif {
    private static final Logger logger = LoggerFactory.getLogger(ImageExif.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    @Value("${thumbs.dir}")
    private String thumbsDir;
    @Value("${albums.dir}")
    private String albumsDir;
    @Value("${max.thumb.size}")
    private int maxThumbSizeInt;
    @Inject
    private ProcessInfoService processInfoService;

    public Image extractExif(File imgFile) throws ImageProcessingException, IOException, ParseException {
        Image image = new Image();
        image.setName(imgFile.getName());

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imgFile);
            Directory directory = metadata.getDirectory(JpegDirectory.class);

            JpegDescriptor jpegDescriptor = new JpegDescriptor((JpegDirectory) directory);
            image.setImageHeight(Integer.parseInt(jpegDescriptor.getImageHeightDescription().replace(" pixels", "")));
            image.setImageWidth(Integer.parseInt(jpegDescriptor.getImageWidthDescription().replace(" pixels", "")));

            directory = metadata.getDirectory(ExifSubIFDDirectory.class);
            ExifSubIFDDescriptor exifSubIFDDescriptor = new ExifSubIFDDescriptor((ExifSubIFDDirectory) directory);
            image.setExposureTime(exifSubIFDDescriptor.getExposureTimeDescription());
            image.setfNumber(exifSubIFDDescriptor.getFNumberDescription());
            image.setExposureProgram(exifSubIFDDescriptor.getExposureProgramDescription());
            image.setIsoSpeedRatings(Integer.parseInt(exifSubIFDDescriptor.getIsoEquivalentDescription()));
            image.setDateTimeOriginal(sdf.parse(exifSubIFDDescriptor.getDescription(36867)));
            image.setShutterSpeedValue(exifSubIFDDescriptor.getShutterSpeedDescription());
            image.setApertureValue(exifSubIFDDescriptor.getApertureValueDescription());
            image.setExposureBiasValue(exifSubIFDDescriptor.getExposureBiasDescription());
            image.setMeteringMode(exifSubIFDDescriptor.getMeteringModeDescription());
            image.setFlash(exifSubIFDDescriptor.getFlashDescription());
            image.setFocalLength(exifSubIFDDescriptor.getFocalLengthDescription());
            image.setExposureMode(exifSubIFDDescriptor.getExposureModeDescription());
            image.setWhiteBalanceMode(exifSubIFDDescriptor.getWhiteBalanceModeDescription());
            image.setSceneCaptureType(exifSubIFDDescriptor.getSceneCaptureTypeDescription());
            image.setGainControl(exifSubIFDDescriptor.getGainControlDescription());
            image.setContrast(exifSubIFDDescriptor.getContrastDescription());
            image.setSaturation(exifSubIFDDescriptor.getSaturationDescription());
            image.setSharpness(exifSubIFDDescriptor.getSharpnessDescription());
            image.setSubjectDistanceRange(exifSubIFDDescriptor.getSubjectDistanceRangeDescription());
            image.setLensModel(exifSubIFDDescriptor.getDescription(42036));

            directory = metadata.getDirectory(ExifIFD0Directory.class);
            ExifIFD0Descriptor exifIFD0Descriptor = new ExifIFD0Descriptor((ExifIFD0Directory) directory);
            image.setModel(exifIFD0Descriptor.getDescription(272));
            // utilizat in url-ul imaginii si cu impact in browser-cache
//            image.setDateTime(sdf.parse(exifIFD0Descriptor.getDescription(306)));
            image.setDateTime(new Date(imgFile.lastModified()));
        } catch (Exception e) {
            logger.error("Nu s-a putut extrage EXIF pt:\n{}", imgFile.getPath());
            image.setDateTime(new Date(imgFile.lastModified()));
            image.setDateTimeOriginal(image.getDateTime());
            prepareImageDimensions(image, imgFile.getPath());
        }

        File thumb = new File(imgFile.getPath().replaceFirst(albumsDir, thumbsDir));
        if (thumb.exists()) {
            image.setThumbLastModified(new Date(thumb.lastModified()));
        } else {
            image.setThumbLastModified(image.getDateTime());
        }

        return image;
    }

    private void prepareImageDimensions(Image image, String path) {
        try {
//            ProcessBuilder identifyImgDimensions = new ProcessBuilder(
//                    "/home/adr/x.sh", "image_dims", path);
            ProcessBuilder identifyImgDimensions = new ProcessBuilder(
                    "identify", "-format", "%[fx:w] %[fx:h]", path);
            String dimensions = processInfoService.getProcessOutput(identifyImgDimensions);
//            logger.debug("dimensions {} for:\n{}", dimensions, path);
            String[] dims = dimensions.split("\\s");
            image.setImageWidth(Integer.parseInt(dims[0]));
            image.setImageHeight(Integer.parseInt(dims[1]));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("Using default dimensions: {}x{}", maxThumbSizeInt, maxThumbSizeInt);
            image.setImageWidth(maxThumbSizeInt);
            image.setImageHeight(maxThumbSizeInt);
        }
    }

    public void copyExifProperties(Image from, Image to) {
        to.setImageHeight(from.getImageHeight());
        to.setImageWidth(from.getImageWidth());
        to.setExposureTime(from.getExposureTime());
        to.setfNumber(from.getfNumber());
        to.setExposureProgram(from.getExposureProgram());
        to.setIsoSpeedRatings(from.getIsoSpeedRatings());
        to.setDateTimeOriginal(from.getDateTimeOriginal());
        to.setShutterSpeedValue(from.getShutterSpeedValue());
        to.setApertureValue(from.getApertureValue());
        to.setExposureBiasValue(from.getExposureBiasValue());
        to.setMeteringMode(from.getMeteringMode());
        to.setFlash(from.getFlash());
        to.setFocalLength(from.getFocalLength());
        to.setExposureMode(from.getExposureMode());
        to.setWhiteBalanceMode(from.getWhiteBalanceMode());
        to.setSceneCaptureType(from.getSceneCaptureType());
        to.setGainControl(from.getGainControl());
        to.setContrast(from.getContrast());
        to.setSaturation(from.getSaturation());
        to.setSharpness(from.getSharpness());
        to.setSubjectDistanceRange(from.getSubjectDistanceRange());
        to.setLensModel(from.getLensModel());
        to.setModel(from.getModel());
        to.setDateTime(from.getDateTime());
        to.setThumbLastModified(from.getThumbLastModified());
    }
}
