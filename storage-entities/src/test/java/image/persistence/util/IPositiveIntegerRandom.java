package image.persistence.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by adr on 2/26/18.
 */
public interface IPositiveIntegerRandom {
	default int randomPositiveInt() {
		return ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
	}
}
