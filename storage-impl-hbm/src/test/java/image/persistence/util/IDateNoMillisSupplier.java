package image.persistence.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by adr on 2/26/18.
 */
public interface IDateNoMillisSupplier {
	default Date dateNoMilliseconds() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
}
