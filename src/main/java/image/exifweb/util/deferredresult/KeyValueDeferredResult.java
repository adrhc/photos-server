package image.exifweb.util.deferredresult;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adr on 1/27/18.
 */
public class KeyValueDeferredResult<K, V> extends DeferredResult<Map<K, V>> {
	public void setResult(K key, V value) {
		Map<K, V> model = new HashMap<>(1, 1);
		model.put(key, value);
		setResult(model);
	}
}
