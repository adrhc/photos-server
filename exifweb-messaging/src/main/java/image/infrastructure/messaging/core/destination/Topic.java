package image.infrastructure.messaging.core.destination;

import image.infrastructure.messaging.core.consumer.Subscription;
import image.infrastructure.messaging.core.message.Message;
import reactor.core.Disposable;

public interface Topic<S, C extends Enum<C>, M extends Message<S, C>> {
	void emit(M albumEvent);

	Disposable register(Subscription<S, C, M> subscription);
}
