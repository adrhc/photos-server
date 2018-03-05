package image.photos.config;

import image.persistence.entity.AppConfig;
import image.photos.util.converter.ListOfTypesConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AppConfigsEntityToCdmConverter extends
		ListOfTypesConverter<AppConfig, image.cdm.AppConfig> {
	@Autowired
	private AppConfigEntityToCdmConverter appConfigEntityToCdmConverter;

	@Override
	public Converter<AppConfig, image.cdm.AppConfig> typeConverterInstance() {
		return this.appConfigEntityToCdmConverter;
	}
}
