package image.persistence.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by adr on 2/26/18.
 */
public interface IPositiveRandom {
	default int positiveRandom() {
		return ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
	}
}
