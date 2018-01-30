package image.exifweb.util;

/**
 * Created by adr on 1/30/18.
 */
public class ValueHolder<T> {
	private T value;

	public ValueHolder() {
	}

	public ValueHolder(T value) {
		this.value = value;
	}

	public static <T> ValueHolder<T> of(T value) {
		return new ValueHolder<>(value);
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
