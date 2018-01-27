package image.exifweb.util.deferredresult;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by adr on 1/27/18.
 */
public class KeyValueDeferredResult<K, V> extends DeferredResult<Map<K, V>> {
//	private static final Logger logger = LoggerFactory.getLogger(KeyValueDeferredResult.class);

	/**
	 * Besides constructing a new KeyValueDeferredResult<K, V> it also
	 * executes voidConsumer which is considered to run asynchronously.
	 *
	 * @param key
	 * @param supplier
	 * @return
	 */
	public static <K1, V1>
	KeyValueDeferredResult<K1, V1> of(Supplier<V1> supplier, K1 key,
	                                  ThreadPoolTaskExecutor asyncExecutor) {
		KeyValueDeferredResult<K1, V1> deferredResult = new KeyValueDeferredResult<>();
		asyncExecutor.execute(() -> {
//			logger.debug("BEGIN");
			V1 value = supplier.get();
//			logger.debug("Result: {}", value);
			deferredResult.handleResult(key, value);
//			logger.debug("END");
		});
		return deferredResult;
	}

	private void handleResult(K key, V value) {
		Map<K, V> model = new HashMap<>(1, 1);
		model.put(key, value);
		setResult(model);
	}
}
