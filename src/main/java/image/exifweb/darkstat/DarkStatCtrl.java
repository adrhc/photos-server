package image.exifweb.darkstat;

import image.exifweb.appmanager.AppManagerCtrl;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: adr
 * Date: 2/21/15
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/json/darkstat")
public class DarkStatCtrl extends AppManagerCtrl {
	@Inject
	private ApplicationContext ac;

	@PostConstruct
	public void postConstruct() {
		this.appManagerService = ac.getBean(DarkStatService.class);
	}
}
