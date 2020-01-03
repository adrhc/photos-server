package image.infrastructure.messaging.core.message;

/**
 * @param <S> means message identity
 */
public interface Stampable<S> {
	S getStamp();

	void setStamp(S stamp);
}
