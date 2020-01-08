package image.photos.util.function;

public interface UnsafeSupplier<T, E extends Throwable> {
	T get() throws E;
}
