package image.photos.config;

import image.persistence.entity.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppConfigsEntityToCdmConverter implements
		Converter<List<AppConfig>, List<image.cdm.AppConfig>> {
	@Autowired
	private AppConfigEntityToCdmConverter appConfigEntityToCdmConverter;

	@Override
	public List<image.cdm.AppConfig> convert(List<AppConfig> source) {
		return source.stream()
				.map(this.appConfigEntityToCdmConverter::convert)
				.collect(Collectors.toList());
	}
}
