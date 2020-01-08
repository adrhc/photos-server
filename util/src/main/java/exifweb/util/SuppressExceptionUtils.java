package exifweb.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.function.Consumer;

@Slf4j
public class SuppressExceptionUtils {
	public static void ignoreExc(Runnable... runnables) {
		Arrays.stream(runnables).forEach(r -> {
			try {
				r.run();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		});
	}

	public static void ignoreExc(Runnable r, Consumer<Exception> exceptionConsumer) {
		try {
			r.run();
		} catch (Exception e) {
			exceptionConsumer.accept(e);
		}
	}
}
