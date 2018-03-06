package image.photos.config;

import image.persistence.entity.AppConfig;
import image.photos.util.converter.AbstractCopyPropertiesConverter;
import org.springframework.stereotype.Component;

@Component
public class AppConfigEntityToCdmConverter extends
		AbstractCopyPropertiesConverter<AppConfig, image.cdm.AppConfig> {
	@Override
	public image.cdm.AppConfig targetNewInstance() {
		return new image.cdm.AppConfig();
	}
}
