package image.exifweb.util.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToProcessBuilderConverter implements Converter<String, ProcessBuilder> {
	@Override
	public ProcessBuilder convert(String command) {
		return new ProcessBuilder(command);
	}
}
