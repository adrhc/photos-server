package image.exifweb.rxjava;

import io.reactivex.Observable;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by adr on 2/7/18.
 */
public class RxJavaTest {
	private static final Logger logger = LoggerFactory.getLogger(RxJavaTest.class);

	@Test
	public void skipErrorTest() {
		Observable.range(1, 5)
				.doOnNext(i -> {
					if (i == 2) {
						logger.debug("throw error for {}", i);
						throw new UnsupportedOperationException();
					}
				})
				.onErrorResumeNext(t -> {
					logger.error("found error");
					return Observable.empty();
				})
				.subscribe(i -> {
					logger.debug("{}", i);
				});
	}
}
