package image.photos.util.converter;

import image.cdm.AppConfig;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class PhotosConversionSupport {
	@Inject
	private ConversionService conversionService;

	public AppConfig cdmAppConfigOf(image.persistence.entity.AppConfig appConfig) {
		return this.conversionService.convert(appConfig, AppConfig.class);
	}

	public List<AppConfig> cdmAppConfigsOf(List<image.persistence.entity.AppConfig> appConfigs) {
		return (List<AppConfig>) this.conversionService.convert(appConfigs,
				ITypeDescriptors.listOfEntityAppConfig,
				ITypeDescriptors.listOfCdmAppConfig);
	}

	public image.persistence.entity.AppConfig entityAppConfigOf(AppConfig appConfig) {
		return this.conversionService.convert(appConfig, image.persistence.entity.AppConfig.class);
	}

	public List<image.persistence.entity.AppConfig> entityAppConfigsOf(List<AppConfig> appConfigs) {
		return (List<image.persistence.entity.AppConfig>)
				this.conversionService.convert(appConfigs,
						ITypeDescriptors.listOfCdmAppConfig,
						ITypeDescriptors.listOfEntityAppConfig);
	}
}
