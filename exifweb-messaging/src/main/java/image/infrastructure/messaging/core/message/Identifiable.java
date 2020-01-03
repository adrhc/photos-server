package image.infrastructure.messaging.core.message;

public interface Identifiable<T> {
	T getId();

	void setId(T id);
}
