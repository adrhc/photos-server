package image.exifweb.exif;

import image.exifweb.util.json.JsonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: adrian.petre
 * Date: 11/8/13
 * Time: 5:12 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/json/action/exif")
public class ExtractExifCtrl {
	@Inject
	private ExtractExifService extractExifService;

	@RequestMapping(method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void extractExif(@RequestBody JsonValue jsonValue, Model model) {
		if (StringUtils.hasText(jsonValue.getValue())) {
			extractExifService.importAlbumByName(jsonValue.getValue());
			model.addAttribute("message", "Start extracting EXIF for " + jsonValue.getValue() + " ...");
		} else {
			extractExifService.importAllFromAlbumsRoot();
			model.addAttribute("message", "Start extracting EXIF for all ...");
		}
	}
}
