package image.exifweb.image;

import image.exifweb.image.dto.ExifInfo;
import image.exifweb.system.persistence.entities.Image;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by adr on 2/10/18.
 */
public class ImageMetadataEntityToDTOConverter implements
		Converter<Image, ExifInfo> {
	private static final Logger logger = LoggerFactory.getLogger(ImageMetadataEntityToDTOConverter.class);

	@Override
	public ExifInfo convert(Image source) {
		if (source == null) {
			return null;
		}
		ExifInfo exifInfo =
				new ExifInfo();
		try {
			BeanUtils.copyProperties(exifInfo, source);
			BeanUtils.copyProperties(exifInfo, source.getImageMetadata());
			BeanUtils.copyProperties(exifInfo, source.getImageMetadata().getExifData());
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return exifInfo;
	}
}
