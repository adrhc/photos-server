package image.photos.config;

import image.persistence.entity.AppConfig;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
public class AppConfigCdmToEntityConverter implements
		Converter<image.cdm.AppConfig, AppConfig> {
	private static final Logger logger =
			LoggerFactory.getLogger(AppConfigEntityToCdmConverter.class);

	@Override
	public AppConfig convert(image.cdm.AppConfig source) {
		AppConfig appConfig = new AppConfig();
		try {
			BeanUtils.copyProperties(appConfig, source);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return appConfig;
	}
}
