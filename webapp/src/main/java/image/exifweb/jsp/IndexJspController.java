package image.exifweb.jsp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * http://127.0.0.1:8080/exifweb/app/index-jsp
 */
@Controller
public class IndexJspController {
	@RequestMapping("/index-jsp")
	public String index() {
		return "index";
	}
}
