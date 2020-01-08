package image.persistence.entity.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class DateUtils {
	public static Date safeParse(String date, DateTimeFormatter sdf) {
		if (date == null) {
			return null;
		}
		try {
			LocalDateTime localDateTime = LocalDateTime.parse(date, sdf);
			return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public static String safeFormat(Date date, DateTimeFormatter sdf) {
		if (date == null) {
			return null;
		}
		try {
			return sdf.format(date.toInstant());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
}
