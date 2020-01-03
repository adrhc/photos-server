package image.infrastructure.messaging.message;

public interface Identifiable<T> {
	T getId();

	void setId(T id);
}
