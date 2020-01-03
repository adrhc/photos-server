package image.infrastructure.messaging.core.message;

import java.io.Serializable;

/**
 * @param <S> means message stamp
 * @param <C> means message category/type
 */
public interface Message<S, C> extends Stampable<S>, Classifiable<C>, Serializable {
}
