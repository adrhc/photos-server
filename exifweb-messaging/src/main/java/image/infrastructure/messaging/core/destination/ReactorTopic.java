package image.infrastructure.messaging.core.destination;

import image.infrastructure.messaging.core.consumer.Subscription;
import image.infrastructure.messaging.core.message.Message;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.FluxSink;

import javax.annotation.PreDestroy;

@Slf4j
public abstract class ReactorTopic<S, C extends Enum<C>, M extends Message<S, C>>
		implements Topic<S, C, M> {
	private final DirectProcessor<M> topic = DirectProcessor.create();
	private final FluxSink<M> sink = this.topic.sink();

	@Override
	public void emit(M albumEvent) {
		this.sink.next(albumEvent);
	}

	public Disposable register(Subscription<S, C, M> subscription) {
		return subscription.subscribe(this.topic);
	}

	@PreDestroy
	public void preDestroy() {
		log.debug("sink.complete: {}", this.getClass());
		this.sink.complete();
	}
}
