package image.exifweb.pyload;

import image.exifweb.appmanager.AppManagerCtrl;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: adriana
 * Date: 12/1/14
 * Time: 3:38 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/json/pyload")
public class PyLoadCtrl extends AppManagerCtrl {
    @Inject
    private ApplicationContext ac;

    @PostConstruct
    public void postConstruct() {
        this.appManagerService = ac.getBean(PyLoadService.class);
    }
}
