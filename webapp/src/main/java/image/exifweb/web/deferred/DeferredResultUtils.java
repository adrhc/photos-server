package image.exifweb.web.deferred;

import image.exifweb.web.controller.KeyValueDeferredResult;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class DeferredResultUtils {
	public static <R, F> R getOrFail(
			Supplier<R> operation, F failResult,
			KeyValueDeferredResult<String, F> deferredResult) {
		try {
			return operation.get();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			deferredResult.setResult("message", failResult);
		}
		return null;
	}
}
