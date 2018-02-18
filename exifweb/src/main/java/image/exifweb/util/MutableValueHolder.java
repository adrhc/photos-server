package image.exifweb.util;

import java.io.Serializable;

/**
 * Used with rxjava Observable.
 * <p>
 * Created by adr on 1/30/18.
 */
public class MutableValueHolder<T extends Serializable> implements Serializable {
	private T value;

	public MutableValueHolder() {
	}

	public MutableValueHolder(T value) {
		this.value = value;
	}

	public static <T1 extends Serializable> MutableValueHolder<T1> of(T1 value) {
		return new MutableValueHolder<>(value);
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
