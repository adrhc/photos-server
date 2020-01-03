package image.infrastructure.messaging.album;

import image.infrastructure.messaging.album.registration.FilteredTypesAlbumSubscription;
import image.persistence.entity.Album;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneaked;
import static image.infrastructure.messaging.album.AlbumEventTypeEnum.UPDATED;
import static image.infrastructure.messaging.util.ThreadUtils.safeSleep;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
class AlbumTopicTest {
	@Test
	public void albumEventsByTypesTest() {
		Thread mainThread = Thread.currentThread();

		AlbumTopic albumTopic = new AlbumTopic();
		List<Album> newAlbums = new ArrayList<>();
		String stamp = UUID.randomUUID().toString();

		albumTopic.register(new FilteredTypesAlbumSubscription(
				stamp, EnumSet.of(UPDATED),
				flux -> flux
						.doOnNext(ae -> log.debug("doOnNext1: {}", ae))
						.doOnSubscribe(s -> {
							log.debug("[doOnSubscribe]:\n\t{}", s);
							assertNotEquals("main", Thread.currentThread().getName());
						})
						.subscribeOn(Schedulers.parallel())// must be after doOnSubscribe!!!
						.publishOn(Schedulers.elastic())// check doOnNext1 and doOnNext2 threads
						.doOnNext(ae -> {
							log.debug("doOnNext2: {}", ae);
							assertNotEquals("main", Thread.currentThread().getName());
						})
						.subscribe(ae -> {
									log.debug("[subscribe] received {}", ae.getEntity().getName());
									assertNotEquals("main", Thread.currentThread().getName());
									// simulating ong processing
									sneaked(() -> Thread.sleep(1000)).run();
									newAlbums.add(ae.getEntity());
									if (ae.getEntity().getName().equals("STOP")) {
										albumTopic.preDestroy();
										mainThread.interrupt();
									}
								},
								t -> log.error(t.getMessage(), t),
								() -> log.debug("completed"),
								s -> {
									log.debug("subscribed:\n\t{}", s);
									// request required otherwise no event will be received!
									s.request(Long.MAX_VALUE);
								}
						)
		));

		log.debug("before emission");
		IntStream.range(1, 3).forEach(i -> albumTopic.emit(AlbumEvent.builder()
				.type(UPDATED).entity(new Album("gigi " + i)).build()));
		albumTopic.emit(AlbumEvent.builder()
				.type(UPDATED).entity(new Album("STOP")).build());
		log.debug("after emission");

		// just sleeping
		safeSleep(10000);

		// todo: make sure to dispose even when an exception occurs
//		subscription.dispose();

		log.debug(newAlbums.stream().map(Album::getName).collect(Collectors.joining(", ")));
	}

	@Test
	public void missEvents() {
		Thread mainThread = Thread.currentThread();

		AlbumTopic albumTopic = new AlbumTopic();
		List<Album> newAlbums = new ArrayList<>();
		String stamp = UUID.randomUUID().toString();

		Executors.newSingleThreadExecutor().submit(() -> {
			log.debug("\n\t[newSingleThreadExecutor] begin");
			// While sleeping all the below emissions occur so
			// nothing is left when actually subscribing; the
			// subscription is though fine but having nothing
			// to process (on the current thread when
			// publishOnCurrentThread = true or worse on another
			// thread when publishOnCurrentThread = false)
			// the subscribe(...) call is completed then the
			// current thread simply completes.
			// The problem here is that I shouldn't subscribe into
			// a manually created thread but I should use publishOn.
			// So this approach is completely wrong!

			// delays subscription in order to miss emissions
			sneaked(() -> Thread.sleep(1000)).run();

			albumTopic.register(new FilteredTypesAlbumSubscription(
					stamp, EnumSet.of(UPDATED),
					flux -> flux
							.doOnNext(ae -> log.debug("doOnNext: {}", ae))
							.doOnSubscribe(s -> log.debug("[doOnSubscribe]:\n\t{}", s))
							.subscribe(ae -> {
										log.debug("[subscribe] received {}", ae.getEntity().getName());
										newAlbums.add(ae.getEntity());
										if (ae.getEntity().getName().equals("STOP")) {
											albumTopic.preDestroy();
											mainThread.interrupt();
										}
									},
									t -> log.error(t.getMessage(), t),
									() -> log.debug("completed"),
									s -> {
										log.debug("subscribed:\n\t{}", s);
										s.request(Long.MAX_VALUE);
									}
							)
			));

			log.debug("\n\t[newSingleThreadExecutor] end");
		});

/*
		log.debug("wait for the subscribers to connect");
		sneaked(() -> Thread.sleep(1000)).run();
*/

		log.debug("before emission");
		IntStream.range(1, 3).forEach(i -> albumTopic.emit(AlbumEvent.builder()
				.type(UPDATED).entity(new Album("gigi " + i)).build()));
		albumTopic.emit(AlbumEvent.builder()
				.type(UPDATED).entity(new Album("STOP")).build());
		log.debug("after emission");

		// just sleeping
		safeSleep(1000);

		log.debug(newAlbums.stream().map(Album::getName).collect(Collectors.joining(", ")));
	}

	/**
	 * https://stackoverflow.com/questions/59553733/why-subscribeon-method-doesnt-switch-the-context
	 */
	@Test
	public void theSubscribeOnMethod() throws InterruptedException {
		Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);

		final Flux<String> flux = Flux
				.range(1, 2)
				.map(i -> 10 + i)
				.subscribeOn(s)// put after doOnSubscribe in order to use parallel-scheduler
				.doOnSubscribe(sub -> System.out.println(
						"[doOnSubscribe] " + Thread.currentThread().getName()))
				.map(i -> Thread.currentThread().getName() + ", value " + i);

		flux.subscribe(System.out::println);

		Thread.sleep(1000);
	}
}
