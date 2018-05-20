package image.exifweb.appmanager;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 8/2/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppManagerCtrl {
	protected AppManagerService appManagerService;

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/start", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public void start(Model model) throws IOException, InterruptedException {
		if (appManagerService.isRunning()) {
			model.addAttribute("message",
					appManagerService.getAppProcName() + " already running!");
			return;
		}
		appManagerService.start();
		model.addAttribute("message", appManagerService.getAppProcName() + " started!");
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/stop", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public void stop(Model model) throws IOException, InterruptedException {
		if (!appManagerService.isRunning()) {
			model.addAttribute("message",
					appManagerService.getAppProcName() + " NOT running!");
			return;
		}
		appManagerService.stop();
		model.addAttribute("message", appManagerService.getAppProcName() + " stopped!");
	}
}
