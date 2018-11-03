package image.photos.util.conversion;

import image.cdm.AppConfig;
import image.cdm.ICdmEntity;
import image.cdm.image.ExifInfo;
import image.persistence.entity.IStorageEntity;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EntityToCdmConverterFactory implements
		ConverterFactory<IStorageEntity, ICdmEntity>, ConditionalConverter {
	static final Map<Class<? extends IStorageEntity>,
			Class<? extends ICdmEntity>> entityToCdmClasses =
			new HashMap<Class<? extends IStorageEntity>,
					Class<? extends ICdmEntity>>() {{
				put(image.persistence.entity.AppConfig.class, AppConfig.class);
				put(image.persistence.entitytests.image.ExifData.class, ExifInfo.class);
			}};

	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (!IStorageEntity.class.isAssignableFrom(sourceType.getObjectType())) {
			return false;
		}
		Class<?> targetClass = this.entityToCdmClasses.get(sourceType.getObjectType());
		return targetClass.isAssignableFrom(targetType.getObjectType());
	}

	@Override
	public <T extends ICdmEntity> Converter<IStorageEntity, T> getConverter(Class<T> targetType) {
		return (Converter<IStorageEntity, T>) new CopyPropertiesConverter<>(this.entityToCdmClasses);
	}
}
