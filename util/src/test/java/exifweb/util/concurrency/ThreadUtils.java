package exifweb.util.concurrency;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ThreadUtils {
	public static void safeSleep(long millis, String label) {
		log.debug("[{}] sleeping ...", label);
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			log.debug("[{}] done sleeping", label);
		}
	}
}
