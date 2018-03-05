package image.photos.config;

import image.persistence.entity.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppConfigsCdmToEntityConverter implements
		Converter<List<image.cdm.AppConfig>, List<AppConfig>> {
	@Autowired
	private AppConfigCdmToEntityConverter appConfigCdmToEntityConverter;

	@Override
	public List<AppConfig> convert(List<image.cdm.AppConfig> source) {
		return source.stream()
				.map(this.appConfigCdmToEntityConverter::convert)
				.collect(Collectors.toList());
	}
}
