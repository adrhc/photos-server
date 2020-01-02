package image.photos.infrastructure.events;

public interface Identifiable<T> {
	T getId();

	void setId(T id);
}
