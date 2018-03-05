package image.photos.config;

import image.persistence.entity.AppConfig;
import image.photos.util.converter.ListOfTypesConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AppConfigsCdmToEntityConverter extends
		ListOfTypesConverter<image.cdm.AppConfig, AppConfig> {
	@Autowired
	private AppConfigCdmToEntityConverter appConfigCdmToEntityConverter;

	@Override
	public Converter<image.cdm.AppConfig, AppConfig> typeConverterInstance() {
		return this.appConfigCdmToEntityConverter;
	}
}
