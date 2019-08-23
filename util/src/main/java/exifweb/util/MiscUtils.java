package exifweb.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Consumer;

public interface MiscUtils {
	Logger logger = LoggerFactory.getLogger(MiscUtils.class);

	default void ignoreExc(Runnable... runnables) {
		Arrays.stream(runnables).forEach(r -> {
			try {
				r.run();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		});
	}

	default void ignoreExc(Runnable r, Consumer<Exception> exceptionConsumer) {
		try {
			r.run();
		} catch (Exception e) {
			exceptionConsumer.accept(e);
		}
	}

	default Date safeDateParse(String s, SimpleDateFormat sdf) {
		try {
			return sdf.parse(s);
		} catch (Exception e) {
			return null;
		}
	}
}
