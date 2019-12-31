package image.photos.util;

import java.io.Serializable;

/**
 * Used with rxjava Observable.
 * <p>
 * Created by adr on 1/30/18.
 */
public class ValueHolder<T extends Serializable> implements Serializable {
	private T value;

	public ValueHolder() {
	}

	public ValueHolder(T value) {
		this.value = value;
	}

	public static <T1 extends Serializable> ValueHolder<T1> of(T1 value) {
		return new ValueHolder<>(value);
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
