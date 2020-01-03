package image.messaging.message;

public interface Identifiable<T> {
	T getId();

	void setId(T id);
}
