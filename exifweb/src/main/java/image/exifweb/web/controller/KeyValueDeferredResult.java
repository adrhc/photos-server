package image.exifweb.web.controller;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Created by adr on 1/27/18.
 */
public class KeyValueDeferredResult<K, V> extends DeferredResult<Map<K, V>> {
	public static <K1, V1> KeyValueDeferredResult<K1, V1> of(
			Consumer<KeyValueDeferredResult<K1, V1>> asyncProcessing,
			Executor asyncExecutor) {
		KeyValueDeferredResult<K1, V1> deferredResult = new KeyValueDeferredResult<>();
		asyncExecutor.execute(() -> asyncProcessing.accept(deferredResult));
		return deferredResult;
	}

	public void setResult(K key, V value) {
		Map<K, V> model = new HashMap<>(1, 1);
		model.put(key, value);
		setResult(model);
	}
}
