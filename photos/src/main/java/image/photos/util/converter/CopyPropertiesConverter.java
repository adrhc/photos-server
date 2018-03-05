package image.photos.util.converter;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.InvocationTargetException;

public abstract class CopyPropertiesConverter<S, T> implements Converter<S, T> {
	private static final Logger logger =
			LoggerFactory.getLogger(CopyPropertiesConverter.class);

	@Override
	public T convert(S source) {
		T target = supplyTargetInstance();
		try {
			BeanUtils.copyProperties(target, source);
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return target;
	}

	public abstract T supplyTargetInstance();
}
