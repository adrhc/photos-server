package image.infrastructure.messaging.core.message;

import java.io.Serializable;

public interface Message<I, C> extends Identifiable<I>, Classifiable<C>, Serializable {
}
