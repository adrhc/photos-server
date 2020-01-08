package image.exifweb.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.WebRequest;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.Function;
import java.util.function.Supplier;

import static image.persistence.entity.util.DateUtils.safeFormat;

/**
 * For testing checkNotModified the browser caching MUST be on!
 * <p>
 * Created by adr on 2/9/18.
 */
public interface INotModifiedChecker {
	Logger logger = LoggerFactory.getLogger(INotModifiedChecker.class);
	DateTimeFormatter sdf = DateTimeFormatter
			.ofPattern("yyyy.MM.dd HH:mm:ss.SSS").withZone(ZoneOffset.UTC);

	default <T> T checkNotModified(Supplier<Date> lastUpdateSupplier,
			Supplier<T> valueSupplier, WebRequest webRequest) {
		Date lastUpdate = lastUpdateSupplier.get();
		if (lastUpdate != null && webRequest.checkNotModified(lastUpdate.getTime())) {
			logger.trace("browser cache valid since: {}", safeFormat(lastUpdate, sdf));
			return null;
		}
		logger.debug("modified since: {}", lastUpdate == null ? null : safeFormat(lastUpdate, sdf));
		return valueSupplier.get();
	}

	default <T> T checkNotModified(Supplier<T> valueSupplier,
			Function<T, Date> lastUpdateFunction,
			WebRequest webRequest) {
		T value = valueSupplier.get();
		Date lastUpdate = lastUpdateFunction.apply(value);
		if (lastUpdate != null && webRequest.checkNotModified(lastUpdate.getTime())) {
			logger.trace("browser cache valid since: {}", safeFormat(lastUpdate, sdf));
			return null;
		}
		logger.debug("modified since: {}", lastUpdate == null ? null : safeFormat(lastUpdate, sdf));
		return value;
	}
}
