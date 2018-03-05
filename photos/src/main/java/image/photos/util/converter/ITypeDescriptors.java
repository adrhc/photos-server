package image.photos.util.converter;

import image.persistence.entity.AppConfig;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;

public interface ITypeDescriptors {
	TypeDescriptor listOfCdmAppConfig = TypeDescriptor.collection(
			List.class, TypeDescriptor.valueOf(image.cdm.AppConfig.class));
	TypeDescriptor listOfEntityAppConfig = TypeDescriptor.collection(
			List.class, TypeDescriptor.valueOf(AppConfig.class));
}
