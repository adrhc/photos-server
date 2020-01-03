package image.infrastructure.messaging.core.message;

public interface Classifiable<T> {
	T getType();

	void setType(T t);
}
