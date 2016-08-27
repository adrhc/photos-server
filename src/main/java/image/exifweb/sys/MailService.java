package image.exifweb.sys;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 12/13/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class MailService {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public boolean checkMailService() throws IOException, InterruptedException {
        List<String> params = new ArrayList<String>();
        params.add("/usr/sbin/email_notify.sh");
        params.add("by_command");
        String date = sdf.format(new Date());
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
