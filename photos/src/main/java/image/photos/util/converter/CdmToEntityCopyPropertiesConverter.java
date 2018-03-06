package image.photos.util.converter;

import image.cdm.AppConfig;
import image.cdm.ICdmEntity;
import image.cdm.image.ExifInfo;
import image.persistence.entity.IStorageEntity;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * We need it as spring @Component in order to check ConditionalConverter.matches(...).
 */
//@Component
public class CdmToEntityCopyPropertiesConverter implements
		Converter<ICdmEntity, IStorageEntity>, ConditionalConverter {
	private static final Logger logger =
			LoggerFactory.getLogger(CdmToEntityCopyPropertiesConverter.class);

	private static final Map<Class<? extends ICdmEntity>,
			Class<? extends IStorageEntity>> cdmToEntityClasses = new HashMap<>();

	@Override
	public IStorageEntity convert(ICdmEntity source) {
		try {
			IStorageEntity target = cdmToEntityClasses.get(source.getClass()).newInstance();
			BeanUtils.copyProperties(target, source);
			return target;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			logger.error(e.getMessage(), e);
			throw new UnsupportedOperationException("Can't convert " + source.getClass().getSimpleName());
		}
	}

	static {
		cdmToEntityClasses.put(AppConfig.class, image.persistence.entity.AppConfig.class);
		cdmToEntityClasses.put(ExifInfo.class, image.persistence.entity.image.ExifData.class);
	}

	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (!ICdmEntity.class.isAssignableFrom(sourceType.getObjectType())) {
			return false;
		}
		Class<?> targetClass = cdmToEntityClasses.get(sourceType.getObjectType());
		return targetClass.isAssignableFrom(targetType.getObjectType());
	}
}
