package image.exifweb.util;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static image.persistence.entity.util.DateUtils.safeFormat;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 12/13/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class MailService {
	private static final DateTimeFormatter sdf =
			DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss").withZone(ZoneOffset.UTC);

	public boolean checkMailService() throws IOException, InterruptedException {
		List<String> params = new ArrayList<>();
		params.add("/usr/sbin/email_notify.sh");
		params.add("by_command");
		String date = safeFormat(new Date(), sdf);
		params.add("[" + date + "] NSA310 mail service status is ok");
		params.add("[" + date + "] NSA310 mail service status is ok");
		ProcessBuilder ps = new ProcessBuilder(params);
		Process process = ps.start();
		process.waitFor();
		InputStream is = process.getInputStream();
		int available = is.available();
		is.close();
		return available == 0;
	}
}
