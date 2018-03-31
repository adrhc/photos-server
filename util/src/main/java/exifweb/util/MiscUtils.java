package exifweb.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public interface MiscUtils {
	Logger logger = LoggerFactory.getLogger(MiscUtils.class);

	default void ignoreExc(Runnable r) {
		try {
			r.run();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	default Date ignoreExc(String s, SimpleDateFormat sdf) {
		try {
			return sdf.parse(s);
		} catch (Exception e) {
			return null;
		}
	}
}
