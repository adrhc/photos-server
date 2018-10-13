package image.exifweb.session;

import image.exifweb.web.security.AuthCheckResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;

public interface SessionCtrl {
	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	AuthCheckResponse getSessionData(WebRequest webRequest, HttpSession httpSession);
}
