package image.infrastructure.messaging.core.message;

/**
 * @param <C> means message category/type
 */
public interface Classifiable<C> {
	C getType();

	void setType(C messageType);
}
