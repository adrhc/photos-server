package image.infrastructure.messaging.core.consumer;

import image.infrastructure.messaging.core.message.Message;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

public interface Subscription<S, C extends Enum<C>, M extends Message<S, C>> {
	Disposable subscribe(Flux<M> flux);
}
