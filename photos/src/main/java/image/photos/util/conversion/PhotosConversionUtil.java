package image.photos.util.conversion;

import image.cdm.AppConfig;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class PhotosConversionUtil {
	private TypeDescriptor listOfCdmAppConfig = TypeDescriptor.collection(
			List.class, TypeDescriptor.valueOf(image.cdm.AppConfig.class));
	private TypeDescriptor listOfEntityAppConfig = TypeDescriptor.collection(
			List.class, TypeDescriptor.valueOf(image.persistence.entity.AppConfig.class));

	@Inject
	private ConversionService cs;

	public List<AppConfig> cdmAppConfigsOf(List<image.persistence.entity.AppConfig> appConfigs) {
		return (List<AppConfig>) this.cs.convert(appConfigs,
				this.listOfEntityAppConfig, this.listOfCdmAppConfig);
	}

	public List<image.persistence.entity.AppConfig> entityAppConfigsOf(List<AppConfig> appConfigs) {
		return (List<image.persistence.entity.AppConfig>)
				this.cs.convert(appConfigs, this.listOfCdmAppConfig, this.listOfEntityAppConfig);
	}
}
