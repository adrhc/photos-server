package image.exifweb.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.WebRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * For testing checkNotModified the browser caching MUST be on!
 * <p>
 * Created by adr on 2/9/18.
 */
public interface INotModifiedChecker {
	Logger logger = LoggerFactory.getLogger(INotModifiedChecker.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");

	default <T> T checkNotModified(Supplier<Date> lastUpdateSupplier,
	                               Supplier<T> valueSupplier, WebRequest webRequest) {
		Date lastUpdate = lastUpdateSupplier.get();
		if (lastUpdate != null && webRequest.checkNotModified(lastUpdate.getTime())) {
			logger.trace("browser cache valid since: {}", sdf.format(lastUpdate));
			return null;
		}
		logger.debug("modified since: {}", sdf.format(lastUpdate));
		return valueSupplier.get();
	}

	default <T> T checkNotModified(Supplier<T> valueSupplier,
	                               Function<T, Date> lastUpdateFunction,
	                               WebRequest webRequest) {
		T value = valueSupplier.get();
		Date lastUpdate = lastUpdateFunction.apply(value);
		if (lastUpdate != null && webRequest.checkNotModified(lastUpdate.getTime())) {
			logger.trace("browser cache valid since: {}", sdf.format(lastUpdate));
			return null;
		}
		logger.debug("modified since: {}", sdf.format(lastUpdate));
		return value;
	}
}
