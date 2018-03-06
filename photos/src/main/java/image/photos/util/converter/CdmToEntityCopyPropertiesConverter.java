package image.photos.util.converter;

import image.cdm.ICdmEntity;
import image.persistence.entity.IStorageEntity;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.InvocationTargetException;

/**
 * Used by CdmToEntityConverterFactory
 *
 * @param <T>
 */
public class CdmToEntityCopyPropertiesConverter<T extends IStorageEntity>
		implements Converter<ICdmEntity, T> {
	private static final Logger logger =
			LoggerFactory.getLogger(CdmToEntityCopyPropertiesConverter.class);

	@Override
	public T convert(ICdmEntity source) {
		try {
			IStorageEntity target = CdmToEntityConverterFactory
					.cdmToEntityClasses.get(source.getClass()).newInstance();
			BeanUtils.copyProperties(target, source);
			return (T) target;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			logger.error(e.getMessage(), e);
			throw new UnsupportedOperationException("Can't convert " + source.getClass().getSimpleName());
		}
	}
}
