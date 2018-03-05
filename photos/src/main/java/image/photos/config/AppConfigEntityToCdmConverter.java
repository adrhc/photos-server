package image.photos.config;

import image.persistence.entity.AppConfig;
import image.photos.util.converter.CopyPropertiesConverter;
import org.springframework.stereotype.Component;

@Component
public class AppConfigEntityToCdmConverter extends
		CopyPropertiesConverter<AppConfig, image.cdm.AppConfig> {
	@Override
	public image.cdm.AppConfig targetNewInstance() {
		return new image.cdm.AppConfig();
	}
}
