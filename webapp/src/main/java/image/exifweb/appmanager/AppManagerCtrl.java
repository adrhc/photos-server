package image.exifweb.appmanager;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 8/2/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AppManagerCtrl<T extends AppManagerService> {
	@Inject
	protected T appManagerService;

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/start", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void start(Model model) throws IOException, InterruptedException {
		if (this.appManagerService.isRunning()) {
			model.addAttribute("message",
					this.appManagerService.getAppProcName() + " already running!");
			return;
		}
		this.appManagerService.start();
		model.addAttribute("message", this.appManagerService.getAppProcName() + " started!");
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/stop", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void stop(Model model) throws IOException, InterruptedException {
		if (!this.appManagerService.isRunning()) {
			model.addAttribute("message",
					this.appManagerService.getAppProcName() + " NOT running!");
			return;
		}
		this.appManagerService.stop();
		model.addAttribute("message", this.appManagerService.getAppProcName() + " stopped!");
	}
}
