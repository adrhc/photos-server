package image.photos.util.conversion;

import image.cdm.AppConfig;
import image.cdm.ICdmEntity;
import image.cdm.image.ExifInfo;
import image.persistence.entity.IStorageEntity;
import image.persistence.entity.image.ExifData;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CdmToEntityConverterFactory implements
		ConverterFactory<ICdmEntity, IStorageEntity>, ConditionalConverter {
	static final Map<Class<? extends ICdmEntity>,
			Class<? extends IStorageEntity>> CORRELATION =
			new HashMap<>() {{
				this.put(AppConfig.class, image.persistence.entity.AppConfig.class);
				this.put(ExifInfo.class, ExifData.class);
			}};

	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (!ICdmEntity.class.isAssignableFrom(sourceType.getObjectType())) {
			return false;
		}
		Class<?> targetClass = CORRELATION.get(sourceType.getObjectType());
		return targetClass.isAssignableFrom(targetType.getObjectType());
	}

	@Override
	public <T extends IStorageEntity> Converter<ICdmEntity, T> getConverter(Class<T> targetType) {
		return (Converter<ICdmEntity, T>) new CopyPropertiesConverter<>(CORRELATION);
	}
}
