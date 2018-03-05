package image.photos.config;

import image.persistence.entity.AppConfig;
import image.photos.util.converter.CopyPropertiesConverter;
import org.springframework.stereotype.Component;

@Component
public class AppConfigCdmToEntityConverter extends
		CopyPropertiesConverter<image.cdm.AppConfig, AppConfig> {
	@Override
	public AppConfig targetNewInstance() {
		return new AppConfig();
	}
}
