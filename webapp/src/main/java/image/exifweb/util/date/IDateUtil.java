package image.exifweb.util.date;

import java.util.Date;
import java.util.stream.Stream;

/**
 * Created by adrianpetre on 12.03.2018.
 */
public interface IDateUtil {
    default Date maxDate(Date... dates) {
        return Stream.of(dates).max(Date::compareTo).get();
    }
}
