package image.exifweb.sys.httpdcheck;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 12/10/13
 * Time: 9:24 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/https/httpdCheck")
public class HttpdCheckController {
    @Inject
    private HttpdCheck httpdCheck;

    @RequestMapping(value = "/getHttpdRestartLogs", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void getHttpdRestartLogs(Model model) throws IOException {
        String logs = httpdCheck.getHttpdRestartLogs();
        model.addAttribute("message", logs);
    }

    @RequestMapping(value = "/checkHttpd", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void checkHttpd(Model model) throws IOException, InterruptedException {
        checkHttpdAsync();
        model.addAttribute("message", "Starting checkHttpd ...");
    }

    @Async
    private void checkHttpdAsync() throws IOException, InterruptedException {
        httpdCheck.checkHttpd();
    }
}
