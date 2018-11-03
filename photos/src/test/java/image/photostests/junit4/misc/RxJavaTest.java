package image.photostests.junit4.misc;

import io.reactivex.Observable;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by adr on 2/7/18.
 */
@Category(MiscTestCategory.class)
public class RxJavaTest {
	private static final Logger logger = LoggerFactory.getLogger(RxJavaTest.class);

	@Test
	public void rxObservableErrorTest() {
		Observable.range(1, 3)
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
