package exifweb.util.random;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by adr on 2/26/18.
 */
public interface IPositiveIntegerRandom {
	default int randomPositiveInt() {
		return randomPositiveInt(Integer.MAX_VALUE);
	}

	default int randomPositiveInt(int max) {
		return ThreadLocalRandom.current().nextInt(0, max);
	}

	default int randomPositiveInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}
}
