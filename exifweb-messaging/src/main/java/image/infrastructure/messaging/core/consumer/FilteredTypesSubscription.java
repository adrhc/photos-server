package image.infrastructure.messaging.core.consumer;

import image.infrastructure.messaging.core.message.Message;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.EnumSet;

@AllArgsConstructor
@RequiredArgsConstructor
public class FilteredTypesSubscription<S, C extends Enum<C>, M extends Message<S, C>>
		implements Subscription<S, C, M> {
	private S stamp;
	@NonNull
	private EnumSet<C> eventTypes;
	@NonNull
	private Subscription<S, C, M> subscription;

	@Override
	public Disposable subscribe(Flux<M> flux) {
		return subscription.subscribe(
				flux.filter(ae -> eventTypes.contains(ae.getType()))
						.filter(ae -> (stamp == null) || ae.getStamp().equals(stamp)));
	}
}
