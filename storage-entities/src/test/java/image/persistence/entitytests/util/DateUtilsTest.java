package image.persistence.entitytests.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static image.persistence.entity.util.DateUtils.safeFormat;
import static image.persistence.entity.util.DateUtils.safeParse;
import static org.junit.Assert.assertEquals;

@Slf4j()
class DateUtilsTest {
	private static final String SDATE = "08.01.2020 22:57:58.123";
	private static final DateTimeFormatter sdf =
			DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS").withZone(ZoneOffset.UTC);

	@Test
	void test() {
		Date date = safeParse(SDATE, sdf);
		assertEquals(safeFormat(date, sdf), SDATE);
	}
}
