package exifweb.util.concurrency;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadUtils {
	public static void safeSleep(long millis) {
		log.debug("sleeping {} milliseconds ...", millis);
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			log.debug("done sleeping");
		}
	}
}
