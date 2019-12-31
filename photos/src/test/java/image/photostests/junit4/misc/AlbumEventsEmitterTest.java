package image.photostests.junit4.misc;

import image.persistence.entity.Album;
import image.photos.events.album.AlbumEvent;
import image.photos.events.album.AlbumEventsEmitter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static image.photos.events.album.EAlbumEventType.ALBUM_IMPORTED;

/**
 * Created by adr on 2/7/18.
 */
@Category(MiscTestCategory.class)
public class AlbumEventsEmitterTest {
	private static final Logger logger = LoggerFactory.getLogger(AlbumEventsEmitterTest.class);

	@Ignore("rxjava observeOn learning test")
	@Test
	public void albumEventsByTypesTest() {
		Thread mainThread = Thread.currentThread();

//		Lock lock = new ReentrantLock();
//		lock.lock();

//		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//			lock.lock();
//			logger.debug("bye");
//			lock.unlock();
//		}));

		AlbumEventsEmitter albumEventsEmitter = new AlbumEventsEmitter();
		List<Album> newAlbums = new ArrayList<>();

		Disposable subscription = albumEventsEmitter
				.albumEventsByTypes(true, ALBUM_IMPORTED)
				.subscribeOn(Schedulers.elastic())
				.doOnNext(ae -> logger.debug("[subscription.doOnNext] {}", ae.getAlbum().getName()))
				.subscribe(
						ae -> {
							logger.debug("[subscribe] {}", ae.getAlbum().getName());
							newAlbums.add(ae.getAlbum());
							if (ae.getAlbum().getName().equals("kent")) {
								mainThread.interrupt();
							}
						},
						t -> {
							logger.error(t.getMessage(), t);
							logger.error("[ALBUM_IMPORTED] newAlbums");
							mainThread.interrupt();
						});

//		Executors.newSingleThreadExecutor().execute(() -> {
		logger.debug("before emission");
		IntStream.range(1, 1000).forEach(i -> albumEventsEmitter.emit(AlbumEvent.builder()
				.type(ALBUM_IMPORTED).album(new Album("gigi " + i)).build()));
		albumEventsEmitter.emit(AlbumEvent.builder()
				.type(ALBUM_IMPORTED).album(new Album("kent")).build());
		logger.debug("after emission");
//		});

		logger.debug("sleeping ...");
		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			logger.debug("done");
		}

		// todo: make sure to dispose even when an exception occurs
		subscription.dispose();
		logger.debug(newAlbums.stream().map(Album::getName).collect(Collectors.joining(", ")));
	}
}
