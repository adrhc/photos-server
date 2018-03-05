package image.photos.util.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ListOfTypesConverter<S, T>
		implements Converter<List<S>, List<T>> {
	private static final Logger logger =
			LoggerFactory.getLogger(ListOfTypesConverter.class);

	@Override
	public List<T> convert(List<S> source) {
		Converter<S, T> converter = typeConverterInstance();
		return source.stream()
				.map(converter::convert)
				.collect(Collectors.toList());
	}

	public abstract Converter<S, T> typeConverterInstance();
}
