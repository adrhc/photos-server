package image.photos.infrastructure.events;

import java.io.Serializable;

public interface Message<I, C> extends Identifiable<I>, Classifiable<C>, Serializable {
}
