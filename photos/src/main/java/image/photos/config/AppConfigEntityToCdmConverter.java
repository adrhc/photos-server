package image.photos.config;

import image.persistence.entity.AppConfig;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
public class AppConfigEntityToCdmConverter implements
		Converter<AppConfig, image.cdm.AppConfig> {
	private static final Logger logger =
			LoggerFactory.getLogger(AppConfigEntityToCdmConverter.class);

	@Override
	public image.cdm.AppConfig convert(AppConfig source) {
		if (source == null) {
			return null;
		}
		image.cdm.AppConfig appConfig = new image.cdm.AppConfig();
		try {
			BeanUtils.copyProperties(appConfig, source);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return appConfig;
	}
}
