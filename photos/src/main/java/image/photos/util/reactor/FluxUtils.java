package image.photos.util.reactor;

import image.photos.util.concurrent.CountDownCoordinator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneak;

@Slf4j
public class FluxUtils {
	/**
	 * Each source (a.k.a. Callable<String>) will run on its own Thread.
	 * Each source will sink to the returned flux.
	 */
	public static <T> Flux<T> create(List<Callable<T>> callables, ExecutorService executorService) {
		return Flux.create(sink -> {
			log.debug("create");

			CountDownCoordinator coordinator = new CountDownCoordinator(callables.size());

			callables.forEach(c -> executorService.submit(() -> {
				sink.next(sneak(c::call));
				if (coordinator.advanceAndReportIfNoMore()) {
					sink.complete();
					log.debug("flux completed");
				}
			}));

			log.debug("flux created");
		});
	}
}
