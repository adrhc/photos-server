package image.photos.infrastructure.events;

public interface Classifiable<T> {
	T getType();

	void setType(T t);
}
