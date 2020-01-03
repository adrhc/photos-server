package image.messaging.message;

public interface Classifiable<T> {
	T getType();

	void setType(T t);
}
