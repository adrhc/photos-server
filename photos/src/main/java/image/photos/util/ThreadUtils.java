package image.photos.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadUtils {
	public static void safeSleep(long millis) {
		log.debug("sleeping {} milliseconds ...", millis);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			log.debug("done sleeping");
		}
	}
}
