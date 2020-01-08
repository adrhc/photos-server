package image.photos.util.reactor;

import image.photos.util.concurrent.CountDownCoordinator;
import image.photos.util.function.UnsafeSupplier;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

@Slf4j
public class FluxUtils {
	/**
	 * Each source (a.k.a. Callable<String>) will run on its own Thread.
	 * Each source will sink to the returned flux.
	 */
	public static <T, E extends Exception> Flux<T> create(
			List<UnsafeSupplier<T, E>> unsafeSuppliers,
			ExecutorService executorService, Function<Exception, Boolean> continueOnError) {
		return Flux.create(sink -> {
			log.debug("create");

			CountDownCoordinator coordinator = new CountDownCoordinator(unsafeSuppliers.size());

			unsafeSuppliers.forEach(c -> executorService.submit(() -> {
				if (sink.isCancelled()) {
					log.debug("flux cancelled");
					return;
				}
				try {
					sink.next(c.get());
				} catch (Exception e) {
					if (!continueOnError.apply(e)) {
						sink.error(e);
					}
					coordinator.advance();
					return;
				}
				if (coordinator.advanceAndReportIfNoMore()) {
					sink.complete();
					log.debug("flux completed");
				}
			}));

			log.debug("flux created");
		});
	}
}
