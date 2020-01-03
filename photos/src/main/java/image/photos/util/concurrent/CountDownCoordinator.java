package image.photos.util.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CountDownCoordinator {
	private AtomicInteger atomicInteger;

	public CountDownCoordinator(int count) {
		this.atomicInteger = new AtomicInteger(count);
	}

	/**
	 * Guaranteed to return true only once.
	 *
	 * @return true means "stop"
	 */
	public boolean advanceAndReportIfNoMore() {
		return atomicInteger.decrementAndGet() == 0;
	}
}
