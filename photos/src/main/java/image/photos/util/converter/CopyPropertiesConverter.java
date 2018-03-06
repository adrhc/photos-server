package image.photos.util.converter;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class CopyPropertiesConverter<S, T> implements Converter<S, T> {
	private static final Logger logger =
			LoggerFactory.getLogger(CopyPropertiesConverter.class);

	private Map<Class<? extends S>, Class<? extends T>> convertionClasses;

	CopyPropertiesConverter(
			Map<Class<? extends S>, Class<? extends T>> convertionClasses) {
		this.convertionClasses = convertionClasses;
	}

	@Override
	public T convert(S source) {
		try {
			T target = this.convertionClasses.get(source.getClass()).newInstance();
			BeanUtils.copyProperties(target, source);
			return (T) target;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			logger.error(e.getMessage(), e);
			throw new UnsupportedOperationException("Can't convert " + source.getClass().getSimpleName());
		}
	}
}
