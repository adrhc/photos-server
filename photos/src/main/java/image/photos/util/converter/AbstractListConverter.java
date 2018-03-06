package image.photos.util.converter;

import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractListConverter<S, T>
		implements Converter<List<S>, List<T>> {

	@Override
	public List<T> convert(List<S> source) {
		Converter<S, T> converter = typeConverterInstance();
		return source.stream()
				.map(converter::convert)
				.collect(Collectors.toList());
	}

	public abstract Converter<S, T> typeConverterInstance();
}
