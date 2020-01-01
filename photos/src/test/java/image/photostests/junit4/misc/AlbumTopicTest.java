package image.photostests.junit4.misc;

import image.persistence.entity.Album;
import image.photos.events.album.AlbumEvent;
import image.photos.events.album.AlbumTopic;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneaked;
import static image.photos.events.album.AlbumEventTypeEnum.UPDATED;
import static image.photos.util.ThreadUtils.safeSleep;

/**
 * Created by adr on 2/7/18.
 */
@Category(MiscTestCategory.class)
@Slf4j
public class AlbumTopicTest {
	@Test
	public void albumEventsByTypesTest() {
		Thread mainThread = Thread.currentThread();

		AlbumTopic albumTopic = new AlbumTopic();
		List<Album> newAlbums = new ArrayList<>();

		albumTopic
				.albumEventsByTypes(false, EnumSet.of(UPDATED))
				.doOnNext(ae -> log.debug("doOnNext1: {}", ae))// after publishOn uses publishOn
				.doOnSubscribe(s -> {
					log.debug("[doOnSubscribe]:\n\t{}", s);
					assert !Thread.currentThread().getName().equals("main");
				})
				.subscribeOn(Schedulers.parallel())// must be after doOnSubscribe!!!
				.publishOn(Schedulers.elastic())// put between doOnNext1 and doOnNext2
				.doOnNext(ae -> {
					log.debug("doOnNext2: {}", ae);
					assert !Thread.currentThread().getName().equals("main");
				})// after publishOn uses publishOn
				.subscribe(ae -> {
							log.debug("[subscribe] received {}", ae.getAlbum().getName());
							assert !Thread.currentThread().getName().equals("main");
							// simulating ong processing
							sneaked(() -> Thread.sleep(1000)).run();
							newAlbums.add(ae.getAlbum());
							if (ae.getAlbum().getName().equals("STOP")) {
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
				);

		log.debug("before emission");
		IntStream.range(1, 3).forEach(i -> albumTopic.emit(AlbumEvent.builder()
				.type(UPDATED).album(new Album("gigi " + i)).build()));
		albumTopic.emit(AlbumEvent.builder()
				.type(UPDATED).album(new Album("STOP")).build());
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

			albumTopic
					.albumEventsByTypes(false, EnumSet.of(UPDATED))
					.doOnNext(ae -> log.debug("doOnNext: {}", ae))// after publishOn uses publishOn
					.doOnSubscribe(s -> log.debug("[doOnSubscribe]:\n\t{}", s))
					.subscribe(ae -> {
								log.debug("[subscribe] received {}", ae.getAlbum().getName());
								newAlbums.add(ae.getAlbum());
								if (ae.getAlbum().getName().equals("STOP")) {
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
					);

			log.debug("\n\t[newSingleThreadExecutor] end");
		});

/*
		log.debug("wait for the subscribers to connect");
		sneaked(() -> Thread.sleep(1000)).run();
*/

		log.debug("before emission");
		IntStream.range(1, 3).forEach(i -> albumTopic.emit(AlbumEvent.builder()
				.type(UPDATED).album(new Album("gigi " + i)).build()));
		albumTopic.emit(AlbumEvent.builder()
				.type(UPDATED).album(new Album("STOP")).build());
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