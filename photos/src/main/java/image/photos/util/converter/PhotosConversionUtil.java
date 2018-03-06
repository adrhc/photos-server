package image.photos.util.converter;

import image.cdm.AppConfig;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class PhotosConversionUtil implements ITypeDescriptors {
	@Inject
	private ConversionService cs;

	public List<AppConfig> cdmAppConfigsOf(List<image.persistence.entity.AppConfig> appConfigs) {
		return (List<AppConfig>) this.cs.convert(appConfigs,
				listOfEntityAppConfig, listOfCdmAppConfig);
	}

	public List<image.persistence.entity.AppConfig> entityAppConfigsOf(List<AppConfig> appConfigs) {
		return (List<image.persistence.entity.AppConfig>)
				this.cs.convert(appConfigs, listOfCdmAppConfig, listOfEntityAppConfig);
	}
}
