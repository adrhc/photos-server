package image.exifweb.stunnel;

import image.exifweb.appmanager.AppManagerCtrl;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 7/2/14
 * Time: 9:11 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/json/stunnel")
public class StunnelCtrl extends AppManagerCtrl {
	@Inject
	private ApplicationContext ac;

	@PostConstruct
	public void postConstruct() {
		this.appManagerService = ac.getBean(StunnelService.class);
	}
}
